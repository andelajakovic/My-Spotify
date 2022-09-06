package com.example.myspotify.ui.prelogin.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myspotify.Config
import com.example.myspotify.data.model.User
import com.example.myspotify.network.HerokuApiService
import com.example.myspotify.ui.state.ButtonState
import com.example.myspotify.ui.state.SignupError
import com.example.myspotify.ui.state.SignupState
import com.example.myspotify.util.HashUtils
import com.example.myspotify.util.UserDataValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(private val herokuApiService: HerokuApiService) : ViewModel() {

    private val _signupState = MutableLiveData<SignupState>()
    val signupState: LiveData<SignupState> = _signupState

    private val _continueButtonState = MutableLiveData(ButtonState(false))
    val continueButtonState: LiveData<ButtonState> = _continueButtonState

    private val hashUtils: HashUtils = HashUtils()
    private val userDataValidator: UserDataValidator = UserDataValidator()

    fun addNewUser(user: User) {
        viewModelScope.launch {
            try {
                addNewUserInternal(user.copy(password = hashUtils.sha256(user.password)))
            } catch (throwable: Throwable) {
                _signupState.postValue(SignupState(isSuccess = false, error = SignupError.UNEXPECTED_ERROR))
            }
        }
    }

    private suspend fun addNewUserInternal(user: User) {
        if (userExists(user)) {
            _signupState.postValue(SignupState(isSuccess = false, error = SignupError.USER_ALREADY_EXISTS))
        } else {
            registerUser(user)
            _signupState.postValue(SignupState(isSuccess = true))
        }
    }

    private suspend fun registerUser(user: User): User {
        return herokuApiService.registerUser(user)
    }

    private suspend fun userExists(user: User): Boolean {
        return herokuApiService.checkIfUserExists(user).isNotEmpty()
    }

    fun updateButtonState(name: String, email: String, password: String) {
        if (name.isEmpty() || !userDataValidator.validateEmail(email) || password.length < Config.MINIMUM_PASSWORD_LENGTH) {
            _continueButtonState.value = ButtonState(false)
        } else {
            _continueButtonState.value = ButtonState(true)
        }
    }

}