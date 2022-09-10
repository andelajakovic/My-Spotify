package com.example.myspotify.ui.prelogin.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.myspotify.Config
import com.example.myspotify.data.local.ApplicationStorage
import com.example.myspotify.data.model.AccessToken
import com.example.myspotify.data.model.Artist
import com.example.myspotify.data.model.ArtistFollowedByUser
import com.example.myspotify.network.HerokuApiService
import com.example.myspotify.network.SpotifyApiService
import com.example.myspotify.ui.state.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseArtistsViewModel @Inject constructor(
    private val herokuApiService: HerokuApiService,
    private val spotifyApiService: SpotifyApiService,
    private val applicationStorage: ApplicationStorage
) : ViewModel() {

    companion object {
        const val minimumArtistsToChoose = Config.MINIMUM_ARTISTS_TO_CHOOSE
    }

    private val _relatedArtistsLoadingState = MutableLiveData<LoadingState>()
    val relatedArtistsLoadingState: LiveData<LoadingState> = _relatedArtistsLoadingState

    private val _artists = MutableLiveData<ListState>()
    val artists: LiveData<ListState> = _artists

    private val _searchedArtists = MutableLiveData<ListState>()
    val searchedArtists: LiveData<ListState> = _searchedArtists

    private val _continueButtonState = MutableLiveData(ButtonState(false))
    val continueButtonState: LiveData<ButtonState> = _continueButtonState

    private val _searchArtistsLoadingState = MutableLiveData<LoadingState>()
    val searchArtistsLoadingState: LiveData<LoadingState> = _searchArtistsLoadingState

    private val _navigationState = MutableLiveData(NavigationState.NONE)
    val navigationState: LiveData<NavigationState> = _navigationState

    private val accessToken: AccessToken = AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN)


    init {
        updateRelatedToSelectedArtists()
    }

    private fun updateRelatedToSelectedArtists() {
        val selectedArtists = getSelectedArtists()
        val relatedArtistsId = selectedArtists.lastOrNull()?.artist?.id ?: Config.RELATED_ARTISTS_ID
        viewModelScope.launch {
            _relatedArtistsLoadingState.value = LoadingState.LOADING
            try {
                _artists.value = ListState(mutableListOf(*selectedArtists.toTypedArray(), *getRelatedArtists(relatedArtistsId).toTypedArray()), isVisible = true)
                _searchedArtists.value = ListState(data = mutableListOf(), isVisible = false)

                _relatedArtistsLoadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                _relatedArtistsLoadingState.value = LoadingState.ERROR
            }
        }
    }

    private fun getSelectedArtists(): List<ArtistState> {
        return artists.value?.data?.filter {
            it.isSelected
        } ?: emptyList()
    }

    private suspend fun getRelatedArtists(referencedArtistId: String): List<ArtistState> {
        return spotifyApiService.getArtists("${accessToken.type} ${accessToken.value}", referencedArtistId).artists.map {
            ArtistState(artist = Artist(id = it.id, imageUrl = it.images?.firstOrNull()?.url, name = it.name))
        }.filterNot {
            getSelectedArtists().map { artist ->
                artist.artist.id
            }.contains(it.artist.id)
        }.toList()
    }

    fun onArtistSelected(artist: ArtistState) {
        toggleArtist(artist)
    }

    private fun toggleArtist(artistState: ArtistState) {
        _artists.value?.data?.firstOrNull {
            it.artist.id == artistState.artist.id
        }?.also {
            it.isSelected = !it.isSelected
        } ?: _artists.value?.data?.add(ArtistState(artistState.artist, isSelected = true))

        updateButtonState()
    }

    private fun updateButtonState() {
        _continueButtonState.value = ButtonState(getSelectedArtists().size >= minimumArtistsToChoose)
    }

    fun onSearchArtistSelected(artist: ArtistState) {
        toggleArtist(artist)
        viewModelScope.launch {
            try {
                onSearchArtistSelectedInternal(artist)
            } catch (e: Exception) {
                _relatedArtistsLoadingState.value = LoadingState.ERROR
            }
        }
    }

    private suspend fun onSearchArtistSelectedInternal(artistState: ArtistState) {
        _artists.value = ListState(mutableListOf(*getSelectedArtists().toTypedArray(), *getRelatedArtists(artistState.artist.id).toTypedArray()), isVisible = true)
        _searchedArtists.value = ListState(data = mutableListOf(), isVisible = false)
    }

    fun searchArtists(query: String) {
        if (query.isEmpty()) {
            return
        }
        viewModelScope.launch {
            _searchArtistsLoadingState.value = LoadingState.LOADING
            try {
                searchArtistsInternal(query)
                _searchArtistsLoadingState.value = LoadingState.DONE
            } catch (e: Throwable) {
                _searchArtistsLoadingState.value = LoadingState.ERROR
            }
        }
    }

    private suspend fun searchArtistsInternal(query: String) {
        val searchList = mutableListOf<ArtistState>()
        searchList.addAll(
            (spotifyApiService.searchArtists("${accessToken.type} ${accessToken.value}", query).artists.items.filter {
                !(getSelectedArtists().any { artist -> artist.artist.id == it.id })
            }).map {
                ArtistState(artist = Artist(id = it.id, imageUrl = it.images?.firstOrNull()?.url, name = it.name))
            })

        _artists.value = _artists.value?.copy(isVisible = false)
        _searchedArtists.value = ListState(searchList, isVisible = true)
    }

    fun synchronizeSelectedArtists() {
        viewModelScope.launch {
            try {
                getSelectedArtists().map { it.artist.id }.forEach { id -> synchronizeSelectedArtistsInternal(id) }
                applicationStorage.setArtistsChosen(true)
                _navigationState.postValue(NavigationState.NAVIGATE_TO_HOME_ACTIVITY)
            } catch (throwable: Throwable) {
                _relatedArtistsLoadingState.value = LoadingState.ERROR
            }
        }
    }

    private suspend fun synchronizeSelectedArtistsInternal(artistId: String) {
        herokuApiService.followArtist(ArtistFollowedByUser(userId = applicationStorage.getLoggedInUserId(), artistId = artistId))
    }

    fun onBackPressed() {
        if (searchedArtists.value?.isVisible == true) {
            _searchedArtists.value = ListState(data = mutableListOf(), isVisible = false)
            _artists.value = _artists.value?.copy(isVisible = true)
        } else {
            _navigationState.value = NavigationState.BACK
        }
    }

}


