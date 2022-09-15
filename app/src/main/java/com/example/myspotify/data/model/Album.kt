package com.example.myspotify.data.model

import com.example.myspotify.data.model.api.ApiTrack
import com.example.myspotify.data.model.api.common.ApiExternalUrls
import java.io.Serializable

data class Album(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val artists: List<Artist>,
    val albumType: String,
    val releaseDate: String,
    val tracks: List<ApiTrack> = listOf(),
    val isUserLiking: Boolean = false,
    val externalUrl: String
) : Serializable