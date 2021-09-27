package com.clxns.app.data.model.cases


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Disposition(
    @Json(name = "allowFeedback")
    val allowFeedback: Boolean,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "isDeleted")
    val isDeleted: Boolean,
    @Json(name = "name")
    val name: String,
    @Json(name = "setReminder")
    val setReminder: Boolean,
    @Json(name = "type")
    val type: String,
    @Json(name = "updatedAt")
    val updatedAt: String
)