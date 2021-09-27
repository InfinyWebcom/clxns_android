package com.clxns.app.data.model.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActionsData(
    @Json(name = "pendingFollowUp")
    val pendingFollowUp: Int,
    @Json(name = "pendingVisit")
    val pendingVisit: Int
)