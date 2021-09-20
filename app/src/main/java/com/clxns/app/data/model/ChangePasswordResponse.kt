package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangePasswordResponse(
    val error: Boolean,
    val title: String,
    val token: String?,
    @Json(name = "detail")
    val userData: UserData? = null
)