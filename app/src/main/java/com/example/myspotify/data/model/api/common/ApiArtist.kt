package com.example.myspotify.data.model.api.common

import com.squareup.moshi.Json

data class ApiArtist(
    @Json(name = "external_urls")
    val externalUrls: ApiExternalUrls,
    @Json(name = "followers")
    val followers: ApiFollowers?,
    @field:Json(name = "genres")
    val genres: List<String>?,
    @Json(name = "href")
    val href: String,
    @Json(name = "id")
    val id: String,
    @field:Json(name = "images")
    val images: List<ApiImage>?,
    @Json(name = "name")
    val name: String,
    @Json(name = "popularity")
    val popularity: Int?,
    @Json(name = "type")
    val type: String,
    @Json(name = "uri")
    val uri: String
)