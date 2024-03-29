package com.example.myspotify.ui.postlogin.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myspotify.Config
import com.example.myspotify.data.local.ApplicationStorage
import com.example.myspotify.data.model.AccessToken
import com.example.myspotify.data.model.Album
import com.example.myspotify.data.model.Artist
import com.example.myspotify.network.HerokuApiService
import com.example.myspotify.network.SpotifyApiService
import com.example.myspotify.ui.state.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val herokuApiService: HerokuApiService,
    private val spotifyApiService: SpotifyApiService,
    private val applicationStorage: ApplicationStorage
) : ViewModel() {

    private val _recommendedArtists = MutableLiveData<MutableList<Artist>>()
    val recommendedArtists: LiveData<MutableList<Artist>> = _recommendedArtists

    private val _newReleases = MutableLiveData<MutableList<Album>>()
    val newReleases: LiveData<MutableList<Album>> = _newReleases

    private val _albumsFromLikedArtists = MutableLiveData<MutableList<Album>>()
    val albumsFromLikedArtists: LiveData<MutableList<Album>> = _albumsFromLikedArtists

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    lateinit var albumWithDetails: Album

    lateinit var likedArtistIds: MutableList<String>

    private val _albumLoadingState = MutableLiveData<LoadingState>()
    val albumLoadingState: LiveData<LoadingState> = _albumLoadingState

    lateinit var accessToken: AccessToken

    init {
        accessToken = AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN)
        initLists()
    }

    private fun initLists() {
        Log.d("Access token value", accessToken.value)
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            try {
                likedArtistIds = getLikedArtists()
                if (likedArtistIds.isNotEmpty()) {
                    _albumsFromLikedArtists.value = getAlbumsFromLikedArtists()
                } else {
                    _albumsFromLikedArtists.value = mutableListOf()
                }
                _newReleases.value = getNewReleases()
                _recommendedArtists.value = getRecommendedArtists()
                _loadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                _loadingState.value = LoadingState.ERROR
            }
        }
    }

    private suspend fun getLikedArtists(): MutableList<String> {
        return herokuApiService.getUsersFollowings(applicationStorage.getLoggedInUserId()).map { it.artistId }.toMutableList()
    }

    private suspend fun getAlbumsFromLikedArtists(): MutableList<Album> {
        val albums: MutableList<Album> = mutableListOf()
        Log.d("Access token value", accessToken.value)
        for (artistId in likedArtistIds) {
            albums.addAll(spotifyApiService.getArtistAlbums("${accessToken.type} ${accessToken.value}", artistId).items.map {
                Album(id = it.id, imageUrl = it.images.firstOrNull()?.url, name = it.name, artists = it.artists.map { artist ->
                    Artist(id = artist.id, imageUrl = artist.images?.firstOrNull()?.url, name = artist.name, followers = artist.followers?.total)
                }, albumType = it.type, releaseDate = it.releaseDate, externalUrl = it.externalUrls.spotify)
            })
        }
        albums.shuffle()
        return albums.subList(0, 20)
    }

    private suspend fun getNewReleases(): MutableList<Album> {
        return spotifyApiService.getNewReleases("${accessToken.type} ${accessToken.value}").albums.items.map {
            Album(id = it.id, imageUrl = it.images.firstOrNull()?.url, name = it.name, artists = it.artists.map { artist ->
                Artist(id = artist.id, imageUrl = artist.images?.firstOrNull()?.url, name = artist.name, followers = artist.followers?.total)
            }, albumType = it.type, releaseDate = it.releaseDate, externalUrl = it.externalUrls.spotify)
        }.toMutableList()
    }

    private suspend fun getRecommendedArtists(): MutableList<Artist> {
        if (likedArtistIds.isNotEmpty())
            return getRelatedArtists(likedArtistIds.random()).toMutableList()
        else return getRelatedArtists(Config.RELATED_ARTISTS_ID).toMutableList()
    }

    private suspend fun getRelatedArtists(relatedArtistsId: String): List<Artist> {
        return spotifyApiService.getArtists("${accessToken.type} ${accessToken.value}", relatedArtistsId).artists.map {
            Artist(
                id = it.id,
                imageUrl = it.images?.firstOrNull()?.url,
                name = it.name,
                followers = it.followers?.total
            )
        }
    }

//    fun getAlbumDetails(album: Album) {
//        viewModelScope.launch {
//            try {
//                _albumLoadingState.value = LoadingState.LOADING
//                val tracks = trackRepository.getAlbumTracks(AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN), album.id)
//                val artists = artistRepository.getSeveralArtists(AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN), album.artists.joinToString (",") { it.id } )
//                albumWithDetails = Album (id = album.id, imageUrl = album.imageUrl, name = album.name, artists = artists, type = album.type, releaseDate = album.releaseDate, tracks = tracks)
//                _albumLoadingState.value = LoadingState.DONE
//            } catch (e: Exception) {
//                _albumLoadingState.value = LoadingState.ERROR
//            }
//        }
//    }

//    fun clearAlbumLoadingState() {
//        _albumLoadingState.value = LoadingState.NONE
//    }

}
