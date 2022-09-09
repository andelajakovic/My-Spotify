package com.example.myspotify.ui.state

import com.example.myspotify.data.model.Artist

data class ArtistState(
    val artist: Artist,
    var isSelected: Boolean = false
)