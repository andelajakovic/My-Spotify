package com.example.spotifyapi.data.model.remote

import com.squareup.moshi.Json

data class ApiArtistQueryResponse(
    @Json(name = "artists")
    val artists: ApiArtists
)