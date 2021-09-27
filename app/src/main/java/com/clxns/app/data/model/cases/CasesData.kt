package com.clxns.app.data.model.cases

import com.clxns.app.data.model.SubDispositionData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CasesData(
    @Json(name = "applicantPincode")
    val applicantPincode: Int,
    @Json(name = "disposition")
    val disposition: Disposition? = null,
    @Json(name = "fi")
    val fi: Fi? = null,
    @Json(name = "fosAssignedDate")
    val fosAssignedDate: String,
    @Json(name = "loanAccountNo")
    val loanAccountNo: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "plans")
    val plans: List<Any>,
    @Json(name = "srNo")
    val srNo: Int,
    @Json(name = "subDisposition")
    val subDisposition: SubDispositionData? = null,
    @Json(name = "totalDueAmount")
    val totalDueAmount: Int
)