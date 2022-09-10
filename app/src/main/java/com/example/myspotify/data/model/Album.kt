package com.example.myspotify.data.model

import java.io.Serializable

data class Album(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val artists: List<Artist>,
    val type: String,
    val releaseDate: String,
    val tracks: List<Track> = listOf()
) : Serializable