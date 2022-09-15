package com.example.myspotify.data.model

data class SearchItem(
    // common
    val id: String,
    val name: String,
    val type: String,
    val imageUrl: String?,

    // artist
    val followers: Int? = 0,

    // album
    val albumType: String = "",
    val artists: List<Artist> = listOf(),
    val releaseDate: String = "",
    val externalUrl: String = "",
)