package com.clxns.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaseHistoryResponse(

    @Json(name = "data")
    val data: List<Lead>,

    @Json(name = "title")
    val title: String? = null,

    @Json(name = "error")
    val error: Boolean? = null
)
