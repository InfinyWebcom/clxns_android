package com.clxns.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DispositionResponse(
    @Json(name = "title")
    val title :String,
    @Json(name = "error")
    val error:Boolean,
    @Json(name = "data")
    val dispositionData: List<DispositionData>
) {
    @JsonClass(generateAdapter = true)
    data class DispositionData(
        @Json(name = "id")
        val id:Int,
        @Json(name = "name")
        val name :String,
        @Json(name = "subStatus")
        val subDispositionDataList :List<SubDispositionData>
    )
}