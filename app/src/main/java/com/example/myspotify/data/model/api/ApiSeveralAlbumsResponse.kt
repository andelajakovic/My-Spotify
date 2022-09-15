package com.example.myspotify.data.model.api

import com.squareup.moshi.Json

data class ApiSeveralAlbumsResponse (
    @Json(name = "albums")
    val albums: List<ApiAlbum>
)