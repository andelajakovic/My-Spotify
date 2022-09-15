package com.example.myspotify.data.model

data class Track(
    val id: String,
    val name: String,
    val artists: List<Artist>
)