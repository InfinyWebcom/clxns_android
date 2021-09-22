package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HomeStatsData(
    @Json(name = "actionsData")
    val actionsData: ActionsData? = null,
    @Json(name = "stats")
    val stats: StatsData? = null,
    @Json(name = "summaryData")
    val summaryData: SummaryData? = null
)