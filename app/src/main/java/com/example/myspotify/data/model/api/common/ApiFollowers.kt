package com.example.myspotify.data.model.api.common

import com.squareup.moshi.Json

data class ApiFollowers(
    @Json(name = "href")
    val href: Any? = null,
    @Json(name = "total")
    val total: Int
)