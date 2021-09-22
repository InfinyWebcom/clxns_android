package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DispositionResponse(
    @Json(name = "data")
    val data: List<DispositionData>,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "title")
    val title: String
)

@JsonClass(generateAdapter = true)
data class DispositionData(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "subStatus")
    val subDispositionList: List<SubDispositionData>
)