package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubDispositionData(
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "dispositionId")
    val dispositionId: Int,
    @Json(name = "id")
    val id: Int,
    @Json(name = "isDeleted")
    val isDeleted: Boolean,
    @Json(name = "name")
    val name: String,
    @Json(name = "updatedAt")
    val updatedAt: String
)