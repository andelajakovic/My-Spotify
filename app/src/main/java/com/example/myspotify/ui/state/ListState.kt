package com.example.myspotify.ui.state

data class ListState(
    val data: MutableList<ArtistState>,
    val isVisible: Boolean
)