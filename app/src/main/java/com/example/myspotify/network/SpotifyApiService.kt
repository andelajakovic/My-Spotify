package com.example.myspotify.network

import com.example.myspotify.data.model.api.*
import com.example.spotifyapi.data.model.remote.ApiArtistQueryResponse
import com.example.spotifyapi.data.model.remote.ApiArtistsResponse
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

    @GET("search?type=album")
    suspend fun searchAlbums(
        @Header("Authorization") authorizationValue: String,
        @Query("q") query: String
    ): ApiAlbumQueryResponse

    @GET("browse/new-releases?limit=10")
    suspend fun getNewReleases(
        @Header("Authorization") authorizationValue: String
    ): ApiNewReleasesResponse

    @GET("artists/{artist-id}/albums/")
    suspend fun getArtistAlbums(
        @Header("Authorization") authorizationValue: String,
        @Path("artist-id") artistId: String
    ): ApiAlbumsResponse

    @GET("artists")
    suspend fun getSeveralArtists(
        @Header("Authorization") authorizationValue: String,
        @Query("ids") artistIds: String
    ): ApiArtistsResponse

    @GET("albums")
    suspend fun getSeveralAlbums(
        @Header("Authorization") authorizationValue: String,
        @Query("ids") artistIds: String
    ): ApiSeveralAlbumsResponse

    @GET("albums/{album-id}/tracks/")
    suspend fun getAlbumTracks(
        @Header("Authorization") authorizationValue: String,
        @Path("album-id") albumId: String
    ): ApiAlbumTracksResponse

    @GET("artists/{artist-id}/top-tracks?market=HR")
    suspend fun getArtistTopTracks(
        @Header("Authorization") authorizationValue: String,
        @Path("artist-id") artistId: String
    ): ApiArtistTopTracksResponse
}