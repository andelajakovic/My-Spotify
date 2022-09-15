package com.example.myspotify.ui.state

import com.example.myspotify.R

enum class SignupError(private val errorMessageId: Int) : UserError {
    USER_ALREADY_EXISTS(R.string.user_already_exists_error_message),
    UNEXPECTED_ERROR(R.string.unexpected_error_has_occurred_error_message);

    override fun getMessageId(): Int {
        return errorMessageId
    }
}