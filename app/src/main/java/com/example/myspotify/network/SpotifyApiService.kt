package com.example.myspotify.network

import com.example.myspotify.data.model.api.ApiGetRelatedArtistsResponse
import com.example.spotifyapi.data.model.remote.ApiArtistQueryResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApiService {
    @GET("artists/{artist-id}/related-artists/")
    suspend fun getArtists(
        @Header("Authorization") authorizationValue: String,
        @Path("artist-id") artistId: String
    ): ApiGetRelatedArtistsResponse

    @GET("search?type=artist")
    suspend fun searchArtists(
        @Header("Authorization") authorizationValue: String,
        @Query("q") query: String
    ): ApiArtistQueryResponse
}