package com.clxns.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddToPlanModel(

    @Json(name = "data")
    val planDate: PlanData? = null,

    @Json(name = "title")
    val title: String? = null,

    @Json(name = "error")
    val error: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class PlanData(

    @Json(name = "createdAt")
    val createdAt: String? = null,

    @Json(name = "fosId")
    val fosId: Int? = null,

    @Json(name = "id")
    val id: Int? = null,

    @Json(name = "planDate")
    val planDate: String? = null,

    @Json(name = "leadId")
    val leadId: String? = null,

    @Json(name = "updatedAt")
    val updatedAt: String? = null
)

