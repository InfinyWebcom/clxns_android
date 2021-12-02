package com.clxns.app.data.model.home

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DemoCap(
    @Json(name = "mainSupporting")
    var mainSupporting : ArrayList<String>? = ArrayList()
)