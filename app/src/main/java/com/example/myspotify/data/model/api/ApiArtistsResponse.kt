package com.example.spotifyapi.data.model.remote


import com.example.myspotify.data.model.api.common.ApiArtist
import com.squareup.moshi.Json

data class ApiArtistsResponse(
    @Json(name = "artists")
    val artists: List<ApiArtist>
)