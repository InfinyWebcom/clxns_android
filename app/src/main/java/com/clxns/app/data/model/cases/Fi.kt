package com.clxns.app.data.model.cases


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Fi(
    @Json(name = "category")
    val category: String,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "fiImage")
    val fiImage: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "isDeleted")
    val isDeleted: Int,
    @Json(name = "location")
    val location: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "updatedAt")
    val updatedAt: String
)