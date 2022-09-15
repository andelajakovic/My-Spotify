package com.example.myspotify.data.model

data class AccessToken(
    val type: String = "Bearer",
    val value: String
)