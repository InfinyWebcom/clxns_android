package com.clxns.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaseDetailsResponse(

	@Json(name="data")
	val data: Lead? = null,

	@Json(name="title")
	val title: String? = null,

	@Json(name="error")
	val error: Boolean? = null
)
