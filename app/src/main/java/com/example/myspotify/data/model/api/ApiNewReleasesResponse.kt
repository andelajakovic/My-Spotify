package com.example.myspotify.data.model.api

import com.squareup.moshi.Json

data class ApiNewReleasesResponse(
    @Json(name = "albums")
    val albums: ApiAlbums
)