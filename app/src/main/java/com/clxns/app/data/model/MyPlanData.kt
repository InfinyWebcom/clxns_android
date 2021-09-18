package com.clxns.app.data.model;


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
open class MyPlanData(
    @Json(name = "_id")
    val id: String,
    @Json(name = "is_deleted")
    val isDeleted: Boolean,
    @Json(name = "name")
    val name: String,
    @Json(name = "content")
    val content: String,
    @Json(name = "image")
    val image: String,
)
