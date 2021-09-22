package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeStatisticsResponse(
    @Json(name = "title")
    val title: String,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "todayData")
    val todayData: HomeStatsData? = null,
    @Json(name = "weekData")
    val weekData: HomeStatsData? = null,
    @Json(name = "monthData")
    val monthData: HomeStatsData? = null

)