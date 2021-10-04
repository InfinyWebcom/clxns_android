package com.clxns.app.data.model.cases


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdatedContactData(
    @Json(name = "content")
    val content: String,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "leadId")
    val leadId: Int,
    @Json(name = "type")
    val type: String,
    @Json(name = "updatedAt")
    val updatedAt: String
)