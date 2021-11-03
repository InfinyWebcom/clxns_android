package com.clxns.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class SubDispositionData(
    @Json(name = "id")
    val id:Int,
    @Json(name = "name")
    val name :String,
    @Json(name = "dispositionId")
    val dispositionId :Int
) : Serializable