package com.clxns.app.data.model.cases


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CasesResponse(
    @Json(name = "collectable")
    val collectable: Int?,
    @Json(name = "data")
    val casesDataList: List<CasesData>,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "title")
    val title: String,
    @Json(name = "total")
    val total: Int
)