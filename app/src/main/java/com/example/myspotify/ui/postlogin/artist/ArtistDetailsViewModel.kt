package com.example.myspotify.ui.postlogin.artist

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
class ArtistDetailsViewModel @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val herokuApiService: HerokuApiService,
    private val applicationStorage: ApplicationStorage
): ViewModel() {

    private val _artist = MutableLiveData<Artist>()
    val artist: LiveData<Artist> = _artist

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    private val _popularReleases = MutableLiveData<List<Album>>()
    val popularReleases: LiveData<List<Album>> = _popularReleases

    private val accessToken: AccessToken = AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN)

    fun initArtist(artist: Artist) {
        viewModelScope.launch {
            try {

                val usersFollowings = herokuApiService.getUsersFollowings(applicationStorage.getLoggedInUserId())
                val isUserFollowing = usersFollowings.any {
                    it.artistId == artist.id
                }

                val accessToken = AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN)
                val tracks = spotifyApiService.getArtistTopTracks("${accessToken.type} ${accessToken.value}", artist.id)
                _artist.value = Artist(id = artist.id, name = artist.name, imageUrl = artist.imageUrl, tracks = tracks.tracks, followers = artist.followers, isUserFollowing = isUserFollowing)

                initPopularReleasesList(artist)

            } catch (e: Exception) {
                Log.d("Init artist exception", e.toString())
            }
        }
    }

    private fun initPopularReleasesList(artist: Artist) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            try {
                _popularReleases.value = getPopularReleases(artist)
                _loadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                _loadingState.value = LoadingState.ERROR
            }
        }
    }

    private suspend fun getPopularReleases(artist: Artist): List<Album>? {
        return spotifyApiService.getArtistAlbums("${accessToken.type} ${accessToken.value}", artist.id).items.map {
            Album(id = it.id, imageUrl = it.images.firstOrNull()?.url, name = it.name, artists = it.artists.map { artist ->
                Artist(id = artist.id, imageUrl = artist.images?.firstOrNull()?.url, name = artist.name, followers = artist.followers?.total)
            }, albumType = it.albumType, releaseDate = it.releaseDate, externalUrl = it.externalUrls.spotify)
        }.toMutableList().subList(0, 4)
    }

    fun followArtist(artist: Artist) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            try {
                if (artist.isUserFollowing) {
                    if (unfollowArtistInternal(artist)){
                        _artist.value = _artist.value?.copy(isUserFollowing = false)
                    }
                } else {
                    if (followArtistInternal(artist)) {
                        _artist.value = _artist.value?.copy(isUserFollowing = true)
                    }
                }
                _loadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                _loadingState.value = LoadingState.ERROR
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