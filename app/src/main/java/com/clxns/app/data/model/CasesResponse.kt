package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CasesResponse(
    @Json(name = "amountCollected")
    val amountCollected: String?,
    @Json(name = "data")
    val casesDataList: List<CasesData>,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "title")
    val title: String,
    @Json(name = "total")
    val total: Int
)