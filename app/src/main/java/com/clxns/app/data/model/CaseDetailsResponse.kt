package com.clxns.app.data.model

import android.os.Parcel
import android.os.Parcelable
import com.clxns.app.data.model.cases.UpdatedContactData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class CaseDetailsResponse(

	@Json(name="data")
	val data: Lead? = null,

	@Json(name="title")
	val title: String? = null,

	@Json(name="error")
	val error: Boolean? = null,

	@Json(name = "updatedContact")
	val contactDataList: List<UpdatedContactData>
) : Serializable
