package com.clxns.app.data.model.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatsData(
    @Json(name = "cases")
    val cases: Int,
    @Json(name = "pos")
    val pos: Int,
    @Json(name = "totalAmountDue")
    val totalAmountDue: Int,
    @Json(name = "totalColletedAmt")
    val totalCollectedAmt: Int
)