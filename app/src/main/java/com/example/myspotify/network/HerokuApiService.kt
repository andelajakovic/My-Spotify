package com.example.myspotify.network

import com.example.myspotify.data.model.AlbumLikedByUser
import com.example.myspotify.data.model.ArtistFollowedByUser
import com.example.myspotify.data.model.Comment
import com.example.myspotify.data.model.User
import retrofit2.Call
import retrofit2.http.*


interface HerokuApiService {
    @POST("users/exits")
    suspend fun checkIfUserExists(
        @Body post: User
    ): List<User>

    @POST("users/register")
    suspend fun registerUser(
        @Body post: User
    ): User

    @POST("users/login")
    suspend fun loginUser(
        @Body post: User
    ): List<User>

    @POST("users/follow")
    suspend fun followArtist(
        @Body post: ArtistFollowedByUser
    ): ArtistFollowedByUser

    @GET("users/{user_id}/followings/")
    suspend fun getUsersFollowings(
        @Path("user_id") userId: Long
    ): List<ArtistFollowedByUser>

    @DELETE("users/{user_id}/unfollow/{artist_id}")
    fun unfollowArtist(
        @Path("user_id") userId: Long?,
        @Path("artist_id") artistId: String?
    ): Call<ArtistFollowedByUser>

    @POST("users/like")
    suspend fun likeAlbum(
        @Body post: AlbumLikedByUser
    ): AlbumLikedByUser

    @GET("users/{user_id}/likings/")
    suspend fun getUsersLikings(
        @Path("user_id") userId: Long
    ): List<AlbumLikedByUser>


    @DELETE("users/{user_id}/dislike/{album_id}")
    fun dislikeAlbum(
        @Path("user_id") userId: Long?,
        @Path("album_id") album_id: String?
    ): Call<AlbumLikedByUser>

    @POST("comments/send")
    suspend fun sendComment(
        @Body post: Comment
    ): Comment

    @GET("comments/{album_id}")
    suspend fun getComments(
        @Path("album_id") albumId: String
    ): List<Comment>

}