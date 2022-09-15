package com.example.myspotify.data.model

import com.squareup.moshi.Json

data class ArtistFollowedByUser(
    @Json(name = "user_id")
    val userId: Long = 0L,
    @Json(name = "artist_id")
    val artistId: String = "",
    @Json(name = "feedback")
    val feedback: String = ""
)
