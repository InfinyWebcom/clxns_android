package com.clxns.app.data.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForgotPasswordResponse(
    val error: Boolean,
    val title: String,
    val token: String?,
    val flag: String?
)