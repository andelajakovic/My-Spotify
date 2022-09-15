package com.example.myspotify.ui.prelogin.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myspotify.data.local.ApplicationStorage
import com.example.myspotify.data.model.User
import com.example.myspotify.network.HerokuApiService
import com.example.myspotify.ui.state.ButtonState
import com.example.myspotify.ui.state.LoginState
import com.example.myspotify.util.HashUtils
import com.example.myspotify.util.UserDataValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val herokuApiService: HerokuApiService,
    private val applicationStorage: ApplicationStorage
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _continueButtonState = MutableLiveData(ButtonState(false))
    val continueButtonState: LiveData<ButtonState> = _continueButtonState

    private val hashUtils: HashUtils = HashUtils()
    private val userDataValidator: UserDataValidator = UserDataValidator()

    fun verifyUser(email: String, password: String) {
        if (userDataValidator.validateEmail(email)) {
            viewModelScope.launch {
                verifyUserInternal(email, hashUtils.sha256(password))
            }
        } else {
            _loginState.postValue(LoginState(isUserVerified = false))
        }
    }

    private suspend fun verifyUserInternal(email: String, password: String) {
        val userResponse =  herokuApiService.loginUser(User(name = "", email = email, password = password))

        if (userResponse.isNotEmpty()) {
            applicationStorage.storeLoggedInUserId(userResponse[0].id)
            _loginState.postValue(LoginState(isUserVerified = true))
            Log.d("Session user id", applicationStorage.getLoggedInUserId().toString())
        } else {
            _loginState.postValue(LoginState(isUserVerified = false))
        }
    }

    fun updateButtonState(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _continueButtonState.value = ButtonState(false)
        } else {
            _continueButtonState.value = ButtonState(true)
        }
    }
}