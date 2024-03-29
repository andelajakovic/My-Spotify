package com.example.myspotify.ui.postlogin.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myspotify.Config
import com.example.myspotify.data.model.AccessToken
import com.example.myspotify.data.model.Artist
import com.example.myspotify.data.model.SearchItem
import com.example.myspotify.network.SpotifyApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val spotifyApiService: SpotifyApiService
) : ViewModel() {

    private val _searchedItems = MutableLiveData<List<SearchItem>>()
    val searchedItems: LiveData<List<SearchItem>> = _searchedItems

//    private val _searchArtistsLoadingState = MutableLiveData<LoadingState>()
//    val searchArtistsLoadingState: LiveData<LoadingState> = _searchArtistsLoadingState

    private val _filterArtists = MutableLiveData<Boolean>()
    val filterArtists: LiveData<Boolean> = _filterArtists

    private val _filterAlbums = MutableLiveData<Boolean>()
    val filterAlbums: LiveData<Boolean> = _filterAlbums

    lateinit var accessToken: AccessToken

    init {
        accessToken = AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN)
        _filterArtists.value = false
        _filterAlbums.value = false
    }

    fun search(query: String) {
        Log.d("Access token value", accessToken.value)

        if (query.isEmpty()) {
            return
        }
        viewModelScope.launch {
//            _searchArtistsLoadingState.value = LoadingState.LOADING
            try {
                if (filterArtists.value == true && filterAlbums.value == false) {
                    searchArtistsInternal(query)
                } else if (filterArtists.value == false && filterAlbums.value == true) {
                    searchAlbumsInternal(query)
                } else {
                    searchArtistsAndAlbumsInternal(query)
                }
//                _searchArtistsLoadingState.value = LoadingState.DONE
            } catch (e: Throwable) {
//                _searchArtistsLoadingState.value = LoadingState.ERROR
            }
        }
    }

    private suspend fun searchArtistsInternal(query: String) {
        val searchList = mutableListOf<SearchItem>()
        searchList.addAll(
            (spotifyApiService.searchArtists("${accessToken.type} ${accessToken.value}", query).artists.items.map {
                SearchItem(id = it.id, name = it.name, type = it.type, imageUrl = it.images?.firstOrNull()?.url, followers = it.followers?.total)
            })
        )

        _searchedItems.value = searchList
    }

    private suspend fun searchAlbumsInternal(query: String) {
        val searchList = mutableListOf<SearchItem>()
        searchList.addAll(
            (spotifyApiService.searchAlbums("${accessToken.type} ${accessToken.value}", query).albums.items.map {
                SearchItem(id = it.id, name = it.name, type = it.type, imageUrl = it.images.firstOrNull()?.url, artists = it.artists.map { artist ->
                    Artist(id = artist.id, imageUrl = artist.images?.firstOrNull()?.url, name = artist.name, followers = artist.followers?.total)
                }, releaseDate = it.releaseDate, albumType = it.albumType, externalUrl = it.externalUrls.spotify)
            })
        )

        _searchedItems.value = searchList

    }

    private suspend fun searchArtistsAndAlbumsInternal(query: String) {
        val searchList = mutableListOf<SearchItem>()
        searchList.addAll(
            (spotifyApiService.searchArtists("${accessToken.type} ${accessToken.value}", query).artists.items.map {
                SearchItem(id = it.id, name = it.name, type = it.type, imageUrl = it.images?.firstOrNull()?.url, followers = it.followers?.total)
            })
        )

        searchList.addAll(
            (spotifyApiService.searchAlbums("${accessToken.type} ${accessToken.value}", query).albums.items.map {
                SearchItem(id = it.id, name = it.name, type = it.type, imageUrl = it.images.firstOrNull()?.url, artists = it.artists.map { artist ->
                    Artist(id = artist.id, imageUrl = artist.images?.firstOrNull()?.url, name = artist.name, followers = artist.followers?.total)
                }, releaseDate = it.releaseDate, albumType = it.albumType, externalUrl = it.externalUrls.spotify)
            })
        )

        searchList.shuffle()
        _searchedItems.value = searchList

    }

    fun toggleArtistFilter(query: CharSequence) {
        _filterArtists.value = !(filterArtists.value)!!
        search(query.toString())
    }

    fun toggleAlbumFilter(query: CharSequence) {
        _filterAlbums.value = !(filterAlbums.value)!!
        search(query.toString())
    }
}