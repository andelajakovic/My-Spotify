package com.example.myspotify.data.model.api

import com.example.myspotify.data.model.api.common.ApiArtist
import com.squareup.moshi.Json

data class ApiGetRelatedArtistsResponse(
    @field:Json(name = "artists")
    val artists: List<ApiArtist>
)