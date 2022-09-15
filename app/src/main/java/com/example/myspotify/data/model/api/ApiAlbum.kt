package com.example.myspotify.data.model.api

import com.example.myspotify.data.model.api.common.ApiArtist
import com.example.myspotify.data.model.api.common.ApiExternalUrls
import com.example.myspotify.data.model.api.common.ApiImage
import com.squareup.moshi.Json

data class ApiAlbum(
    @Json(name = "album_group")
    val albumGroup: String?,
    @Json(name = "album_type")
    val albumType: String,
    @Json(name = "artists")
    val artists: List<ApiArtist>,
    @Json(name = "available_markets")
    val availableMarkets: List<String>?,
    @Json(name = "external_urls")
    val externalUrls: ApiExternalUrls,
    @Json(name = "href")
    val href: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "images")
    val images: List<ApiImage>,
    @Json(name = "name")
    val name: String,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "release_date_precision")
    val releaseDatePrecision: String,
    @Json(name = "total_tracks")
    val totalTracks: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "uri")
    val uri: String
)