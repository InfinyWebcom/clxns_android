package com.clxns.app.data.model.cases

import com.clxns.app.data.model.AdditionalFieldModel
import com.clxns.app.data.model.PaymentModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaseCheckInBody(

    @Json(name = "loanAccountNo")
    var loanAccountNo: String? = null,

    @Json(name = "followUp")
    var followUp: String? = null,

    @Json(name = "additionalField")
    var additionalField: AdditionalFieldModel? = null,

    @Json(name = "comments")
    var comments: String? = null,

    @Json(name = "dispositionId")
    var dispositionId: String? = null,

    @Json(name = "location")
    var location: String? = null,

    @Json(name = "payment")
    var payment: PaymentModel? = null,

    @Json(name = "nextAction")
    var nextAction: String? = null,

    @Json(name = "supporting")
    var supporting: List<String>? = null,

    @Json(name = "subDispositionId")
    var subDispositionId: String? = null
)
