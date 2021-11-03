package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HistoryPaymentData(
    @Json(name = "chequeNo")
    val chequeNo: String,
    @Json(name = "collectedAmt")
    val collectedAmt: Int,
    @Json(name = "id")
    val id: Int,
    @Json(name = "paymentMode")
    val paymentMode: String,
    @Json(name = "refNo")
    val refNo: String,
    @Json(name = "recoveryDate")
    val recoveryDate: String,
    @Json(name = "supporting")
    val supporting: String
)