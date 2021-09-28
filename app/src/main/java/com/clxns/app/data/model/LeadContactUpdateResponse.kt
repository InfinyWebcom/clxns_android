package com.clxns.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeadContactUpdateResponse(

    @Json(name = "error")
    val error: Boolean? = null,

    @Json(name = "title")
    val title: String? = null,

    @Json(name = "errors")
    val errors: Errors? = null
)

@JsonClass(generateAdapter = true)
data class Errors(

    @Json(name = "errors")
    val errors: List<ErrorsItem?>? = null
)

@JsonClass(generateAdapter = true)
data class ErrorsItem(

    @Json(name = "msg")
    val msg: String? = null,

    @Json(name = "param")
    val param: String? = null,

    @Json(name = "location")
    val location: String? = null,

    @Json(name = "value")
    val value: String? = null
)


