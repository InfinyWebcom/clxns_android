package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CasesData(
    @Json(name = "loanAccountNo")
    val loanAccountNo: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "paymentStatus")
    val paymentStatus: String,
    @Json(name = "totalDueAmount")
    val totalDueAmount: Int
)