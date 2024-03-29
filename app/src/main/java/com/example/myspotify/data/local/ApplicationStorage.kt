package com.example.myspotify.data.local

import android.content.SharedPreferences

class ApplicationStorage (private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ARTISTS_CHOSEN_FLAG = "artists_chosen_flag"
    }

    fun storeLoggedInUserId(id: Long) = saveLong(KEY_USER_ID, id)
    fun getLoggedInUserId() = getLong(KEY_USER_ID)
    fun isLoggedIn(): Boolean = getLoggedInUserId() > 0
    fun setArtistsChosen(isArtistChosen: Boolean) = saveBoolean(KEY_ARTISTS_CHOSEN_FLAG, isArtistChosen)
    fun isArtistsChosen() = getBoolean(KEY_ARTISTS_CHOSEN_FLAG)

    private fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    private fun saveLong(key: String, value: Long) {
        sharedPreferences.edit()
            .putLong(key, value)
            .apply()
    }

    private fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    private fun saveBoolean(key: String, value: Boolean) {
        return sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }

}