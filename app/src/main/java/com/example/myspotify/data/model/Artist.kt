package com.example.myspotify.data.model

import com.example.myspotify.data.model.api.ApiTrackX
import java.io.Serializable

data class Artist(
    val id: String,
    val imageUrl: String?,
    val name: String,
    val tracks: List<ApiTrackX> = listOf(),
    val followers: Int?,
    val isUserFollowing: Boolean = false
) : Serializable