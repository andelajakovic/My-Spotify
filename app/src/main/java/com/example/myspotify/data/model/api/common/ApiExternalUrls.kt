package com.example.myspotify.data.model.api.common

import com.squareup.moshi.Json

data class ApiExternalUrls(
    @Json(name = "spotify")
    val spotify: String
)