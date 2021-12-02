package com.clxns.app.data.model.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeStatisticsResponse(
    @Json(name = "title")
    val title : String,
    @Json(name = "error")
    val error : Boolean,
    @Json(name = "monthData")
    val monthData : HomeStatsData? = null,
    @Json(name = "todayData")
    val todayData : HomeStatsData? = null,
    @Json(name = "weekData")
    val weekData : HomeStatsData? = null
)