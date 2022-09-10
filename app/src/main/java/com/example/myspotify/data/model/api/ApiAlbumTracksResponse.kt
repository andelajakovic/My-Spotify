package com.example.myspotify.data.model.api

import com.squareup.moshi.Json

data class ApiAlbumTracksResponse(
    @Json(name = "href")
    val href: String,
    @Json(name = "items")
    val items: List<ApiTrack>,
    @Json(name = "limit")
    val limit: Int,
    @Json(name = "next")
    val next: Any?,
    @Json(name = "offset")
    val offset: Int,
    @Json(name = "previous")
    val previous: Any?,
    @Json(name = "total")
    val total: Int
)