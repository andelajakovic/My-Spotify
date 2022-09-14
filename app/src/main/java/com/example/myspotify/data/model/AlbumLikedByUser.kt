package com.example.myspotify.data.model

import com.squareup.moshi.Json

data class AlbumLikedByUser(
    @Json(name = "user_id")
    val userId: Long = 0L,
    @Json(name = "album_id")
    val albumId: String = "",
    @Json(name = "feedback")
    val feedback: String = ""
)