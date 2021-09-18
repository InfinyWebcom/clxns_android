package com.clxns.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyPlanResponse(
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "data")
    val data: List<MyPlanData>,
    @Json(name = "total")
    val totalCount: Int
)