package com.example.spotifyapi.data.model.remote

import com.example.myspotify.data.model.api.common.ApiArtist
import com.squareup.moshi.Json

data class ApiArtists(
    @Json(name = "href")
    val href: String,
    @field:Json(name = "items")
    val items: List<ApiArtist>,
    @Json(name = "limit")
    val limit: Int,
    @Json(name = "next")
    val next: String,
    @Json(name = "offset")
    val offset: Int,
    @Json(name = "previous")
    val previous: Any? = null,
    @Json(name = "total")
    val total: Int
)