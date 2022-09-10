package com.example.myspotify.ui.postlogin.logout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myspotify.data.local.ApplicationStorage
import com.example.myspotify.ui.state.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogoutViewModel @Inject constructor(
    private val applicationStorage: ApplicationStorage
) : ViewModel() {

    private val _sessionState = MutableLiveData<SessionState>()
    val sessionState: LiveData<SessionState> = _sessionState

    init {
        applicationStorage.storeLoggedInUserId(0)
        applicationStorage.setArtistsChosen(false)
        _sessionState.value = SessionState.LOGGED_OUT
    }


}