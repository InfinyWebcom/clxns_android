package com.clxns.app.data.model.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatsData(
    @Json(name = "cases")
    val cases : Int,
    @Json(name = "pos")
    val pos : Int? = null,
    @Json(name = "totalAmountDue")
    val totalAmountDue : Int? = null,
    @Json(name = "totalColletedAmt")
    val totalCollectedAmt : Int? = null
)