package com.example.myspotify.ui.state

import com.example.myspotify.ui.state.UserError

data class SignupState(
    val isSuccess: Boolean,
    val error: UserError? = null
)