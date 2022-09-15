package com.example.myspotify.ui.postlogin.library

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
import com.example.myspotify.data.model.ArtistFollowedByUser
import com.example.myspotify.network.HerokuApiService
import com.example.myspotify.network.SpotifyApiService
import com.example.myspotify.ui.state.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import javax.inject.Inject

@HiltViewModel
class MyLibraryViewModel @Inject constructor(
    private val herokuApiService: HerokuApiService,
    private val spotifyApiService: SpotifyApiService,
    private val applicationStorage: ApplicationStorage
) : ViewModel() {

    private val _favoriteArtists = MutableLiveData<MutableList<Artist>>()
    val favoriteArtists: LiveData<MutableList<Artist>> = _favoriteArtists

    private val _likedAlbums = MutableLiveData<MutableList<Album>>()
    val likedAlbums: LiveData<MutableList<Album>> = _likedAlbums

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    lateinit var albumWithDetails: Album

    lateinit var favoriteArtistIds: MutableList<String>
    lateinit var likedAlbumIds: MutableList<String>

    private val _albumLoadingState = MutableLiveData<LoadingState>()
    val albumLoadingState: LiveData<LoadingState> = _albumLoadingState

    lateinit var accessToken: AccessToken

    init {
        accessToken = AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN)
    }

    fun initLists() {
        Log.d("Access token value", accessToken.value)
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            try {
                favoriteArtistIds = getFavoriteArtists()
                likedAlbumIds = getLikedAlbums()

                if (favoriteArtistIds.isEmpty()) {
                    _favoriteArtists.value = mutableListOf()
                } else {
                    _favoriteArtists.value = getArtists()
                }

                if (likedAlbumIds.isEmpty()) {
                    _likedAlbums.value = mutableListOf()
                } else {
                    _likedAlbums.value = getAlbums()
                }

                _loadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                _loadingState.value = LoadingState.ERROR
            }
        }
    }

    private suspend fun getLikedAlbums(): MutableList<String> {
        return herokuApiService.getUsersLikings(applicationStorage.getLoggedInUserId()).map { it.albumId }.toMutableList()
    }

    private suspend fun getFavoriteArtists(): MutableList<String> {
        return herokuApiService.getUsersFollowings(applicationStorage.getLoggedInUserId()).map { it.artistId }.toMutableList()
    }

    private suspend fun getAlbumsFromLikedArtists(): MutableList<Album> {
        val albums: MutableList<Album> = mutableListOf()
        Log.d("Access token value", accessToken.value)
        for (artistId in favoriteArtistIds) {
            albums.addAll(spotifyApiService.getArtistAlbums("${accessToken.type} ${accessToken.value}", artistId).items.map {
                Album(id = it.id, imageUrl = it.images.firstOrNull()?.url, name = it.name, artists = it.artists.map { artist ->
                    Artist(id = artist.id, imageUrl = artist.images?.firstOrNull()?.url, name = artist.name, followers = artist.followers?.total)
                }, albumType = it.type, releaseDate = it.releaseDate, externalUrl = it.externalUrls.spotify)
            })
        }
        albums.shuffle()
        return albums.subList(0, 20)
    }

    private suspend fun getAlbums(): MutableList<Album> {
        return spotifyApiService.getSeveralAlbums("${accessToken.type} ${accessToken.value}", likedAlbumIds.joinToString(",") { it }).albums.map {
            Album(id = it.id, imageUrl = it.images.firstOrNull()?.url, name = it.name, artists = it.artists.map { artist ->
                Artist(id = artist.id, imageUrl = artist.images?.firstOrNull()?.url, name = artist.name, followers = artist.followers?.total)
            }, albumType = it.type, releaseDate = it.releaseDate, externalUrl = it.externalUrls.spotify, isUserLiking = true)
        }.toMutableList()
    }

    private suspend fun getArtists(): MutableList<Artist> {
        Log.d("Access token value", accessToken.value)

        return (spotifyApiService.getSeveralArtists("${accessToken.type} ${accessToken.value}", favoriteArtistIds.joinToString(",") { it }).artists.map {
            Artist(id = it.id, imageUrl = it.images?.firstOrNull()?.url, name = it.name, followers = it.followers?.total, isUserFollowing = true)
        }).toMutableList()
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

    fun followArtist(artist: Artist) {
        viewModelScope.launch {
//            _loadingState.value = LoadingState.LOADING
            try {
                if (artist.isUserFollowing) {
                    if (unfollowArtistInternal(artist)){
                        val artists : MutableList<Artist> = mutableListOf()
                        _favoriteArtists.value?.let { artists.addAll(it) }
                        artists.forEach {
                            if (it.id == artist.id) {
                                it.isUserFollowing = false
                            }
                        }
                        _favoriteArtists.value = artists
                    }
                } else {
                    if (followArtistInternal(artist)) {
                        val artists : MutableList<Artist> = mutableListOf()
                        _favoriteArtists.value?.let { artists.addAll(it) }
                        artists.forEach {
                            if (it.id == artist.id) {
                                it.isUserFollowing = true
                            }
                        }
                        _favoriteArtists.value = artists                    }
                }
//                _loadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                Log.d("Follow artist exception", e.toString())
//                _loadingState.value = LoadingState.ERROR
            }
        }

    }

    private suspend fun unfollowArtistInternal(artist: Artist): Boolean {
        val response = herokuApiService.unfollowArtist(applicationStorage.getLoggedInUserId(), artist.id)
        return response.awaitResponse().isSuccessful
    }

    private suspend fun followArtistInternal(artist: Artist) : Boolean {
        val response = herokuApiService.followArtist(ArtistFollowedByUser(userId = applicationStorage.getLoggedInUserId(), artistId = artist.id))
        return response.feedback == "artist_followed"
    }

}
