package com.example.myspotify.data.model

import com.squareup.moshi.Json

data class User(
    @Json(name = "id")
    val id: Long = 0L,
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "password")
    val password: String,
    @Json(name = "feedback")
    val feedback: String = ""


) {
    override fun toString(): String {
        return "User(id=$id, name='$name', email='$email', password='$password', feedback='$feedback')"
    }
}
