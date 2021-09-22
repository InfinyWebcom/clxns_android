package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SummaryData(
    @Json(name = "brokenPTP")
    val brokenPTP: Int,
    @Json(name = "callBack")
    val callBack: Int,
    @Json(name = "customerDeceased")
    val customerDeceased: Int,
    @Json(name = "customerNotFound")
    val customerNotFound: Int,
    @Json(name = "denialRTP")
    val denialRTP: Int,
    @Json(name = "dispute")
    val dispute: Int,
    @Json(name = "partiallyRecovered")
    val partiallyRecovered: Int,
    @Json(name = "promiseToPay")
    val promiseToPay: Int,
    @Json(name = "recovered")
    val recovered: Int,
    @Json(name = "settlementForeclosure")
    val settlementForeclosure: Int,
    @Json(name = "totalCases")
    val totalCases: Int
)