package com.example.myspotify.util

class UserDataValidator {
    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})")
    }

    fun validateEmail(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }
}