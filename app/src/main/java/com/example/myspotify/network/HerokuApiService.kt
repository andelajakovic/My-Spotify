package com.example.myspotify.network

import com.example.myspotify.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
}