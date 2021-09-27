package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FISBankResponse(
    @Json(name = "data")
    val bankData: List<BankData>,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "title")
    val title: String
)