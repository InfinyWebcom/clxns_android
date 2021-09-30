package com.clxns.app.data.model

import com.clxns.app.data.model.cases.Disposition
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HistoryData(
    @Json(name = "additionalField")
    val additionalField: String?,
    @Json(name = "comments")
    val comments: String?,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "dispositionId")
    val dispositionId: Int?,
    @Json(name = "fileName")
    val fileName: String?,
    @Json(name = "followUp")
    val followUp: String?,
    @Json(name = "id")
    val id: Int,
    @Json(name = "leadId")
    val leadId: Int,
    @Json(name = "location")
    val location: String?,
    @Json(name = "nextAction")
    val nextAction: String?,
    @Json(name = "subDispositionId")
    val subDispositionId: Int?,
    @Json(name = "updatedAt")
    val updatedAt: String,
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "dispositions")
    val dispositions:Disposition?,
    @Json(name = "subDisposition")
    val subDisposition:SubDispositionData?
)