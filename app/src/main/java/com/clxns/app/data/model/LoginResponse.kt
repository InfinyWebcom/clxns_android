package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "data")
    val loginData: UserData?,
    val error: Boolean?,
    val title: String?,
    val token: String?,
    val flag: String?
)