package com.example.myspotify.data.model.api

import com.squareup.moshi.Json

data class ApiArtistTopTracksResponse(
    @Json(name = "tracks")
    val tracks: List<ApiTrackX>
)