package com.example.myspotify.data.model.api.common

import com.squareup.moshi.Json

data class ApiImage(
    @Json(name = "height")
    val height: Int,
    @Json(name = "url")
    val url: String,
    @Json(name = "width")
    val width: Int
)