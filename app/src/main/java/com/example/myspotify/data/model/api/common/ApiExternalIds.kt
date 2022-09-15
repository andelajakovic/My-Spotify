package com.example.myspotify.data.model.api.common

import com.squareup.moshi.Json

data class ApiExternalIds(
    @Json(name = "isrc")
    val isrc: String
)