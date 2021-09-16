package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "data")
    val loginData: UserData? = null,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "title")
    val title: String,
    @Json(name = "token")
    val token: String = "",
    val flag: String = "",

    )