package com.example.myspotify.data.model

import java.io.Serializable

data class Artist(
    val id: String,
    val imageUrl: String?,
    val name: String,
) : Serializable