package com.example.myspotify.ui.prelogin.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myspotify.data.local.ApplicationStorage
import com.example.myspotify.ui.state.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val applicationStorage: ApplicationStorage
) : ViewModel() {
    private val _sessionState = MutableLiveData<SessionState>()
    val sessionState: LiveData<SessionState> = _sessionState

    init {
        if (applicationStorage.isLoggedIn() && applicationStorage.isArtistsChosen()) {
            _sessionState.value = SessionState.LOGGED_IN
        } else if (applicationStorage.isLoggedIn() && !applicationStorage.isArtistsChosen()) {
            _sessionState.value = SessionState.CHOOSE_ARTISTS
        } else {
            _sessionState.value = SessionState.LOGGED_OUT
        }
    }
}