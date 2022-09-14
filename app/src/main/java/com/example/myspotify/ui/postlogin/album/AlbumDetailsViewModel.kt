package com.example.myspotify.ui.postlogin.album

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myspotify.Config
import com.example.myspotify.data.local.ApplicationStorage
import com.example.myspotify.data.model.*
import com.example.myspotify.network.HerokuApiService
import com.example.myspotify.network.SpotifyApiService
import com.example.myspotify.ui.state.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val spotifyApiService: SpotifyApiService,
    private val herokuApiService: HerokuApiService,
    private val applicationStorage: ApplicationStorage
) : ViewModel() {

    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album> = _album

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    lateinit var accessToken: AccessToken

    init {
        accessToken = AccessToken(value = Config.SPOTIFY_ACCESS_TOKEN)
    }

    fun initAlbum(album: Album) {
        viewModelScope.launch {
            try {
                Log.d("Access token value", accessToken.value)

                val usersLikings = herokuApiService.getUsersLikings(applicationStorage.getLoggedInUserId())
                val isUserLiking = usersLikings.any {
                    it.albumId == album.id
                }

                val tracks = spotifyApiService.getAlbumTracks("${accessToken.type} ${accessToken.value}", album.id)
                val artists = spotifyApiService.getSeveralArtists("${accessToken.type} ${accessToken.value}", album.artists.joinToString(",") { it.id })

                _album.value = Album(id = album.id, imageUrl = album.imageUrl, name = album.name, artists = artists.artists.map { artist ->
                    Artist(id = artist.id, imageUrl = artist.images?.firstOrNull()?.url, name = artist.name, followers = artist.followers?.total)
                }, albumType = album.albumType, releaseDate = album.releaseDate, tracks = tracks.items, isUserLiking = isUserLiking, externalUrl = album.externalUrl)
            } catch (e: Exception) {
                Log.d("Init album exception", e.toString())
            }
        }
    }

    fun likeAlbum(album: Album) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            try {
                if (album.isUserLiking) {
                    if (dislikeAlbumInternal(album)) {
                        _album.value = _album.value?.copy(isUserLiking = false)
                    }
                } else {
                    if (likeAlbumInternal(album)) {
                        _album.value = _album.value?.copy(isUserLiking = true)
                    }
                }
                _loadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                _loadingState.value = LoadingState.ERROR
            }
        }

    }


    private suspend fun dislikeAlbumInternal(album: Album): Boolean {
        val response = herokuApiService.dislikeAlbum(applicationStorage.getLoggedInUserId(), album.id)
        return response.awaitResponse().isSuccessful
    }

    private suspend fun likeAlbumInternal(album: Album): Boolean {
        val response = herokuApiService.likeAlbum(AlbumLikedByUser(userId = applicationStorage.getLoggedInUserId(), albumId = album.id))
        return response.feedback == "album_liked"
    }

    fun sendComment(text: String, album: Album) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.LOADING
            try {
                if (sendCommentInternal(text, album)) {
                    getComments(album)
                }

                _loadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                _loadingState.value = LoadingState.ERROR
            }
        }
    }

    private suspend fun sendCommentInternal(text: String, album: Album): Boolean {
        val response = herokuApiService.sendComment(Comment(userId = applicationStorage.getLoggedInUserId(), albumId = album.id, text = text))
        return response.feedback == "comment_sent"
    }

    fun getComments(album: Album) {
        viewModelScope.launch {

            _loadingState.value = LoadingState.LOADING
            try {
                val comments = herokuApiService.getComments(album.id)
                _comments.value = comments

                _loadingState.value = LoadingState.DONE
            } catch (e: Exception) {
                Log.d("Get comments exception", e.toString())
                _loadingState.value = LoadingState.ERROR
            }
        }
    }
}