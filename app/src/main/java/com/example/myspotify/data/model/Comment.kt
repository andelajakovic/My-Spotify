package com.example.myspotify.data.model

import com.squareup.moshi.Json

data class Comment(
    @Json(name = "comment_id")
    val commentId: Long = 0L,
    @Json(name = "user_id")
    val userId: Long = 0L,
    @Json(name = "album_id")
    val albumId: String = "",
    @Json(name = "text")
    val text: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "feedback")
    val feedback: String = ""
)
