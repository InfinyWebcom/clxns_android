package com.clxns.app.data.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdditionalFieldModel(

    @field:SerializedName("recoveredAmount")
    var recoveredAmount: String? = "",

    @field:SerializedName("ref_ChequeNo")
    var refChequeNo: String? = "",

    @field:SerializedName("paymentMode")
    var paymentMode: String? = "",

    @field:SerializedName("ptpAmount")
    var ptpAmount: String? = "",

    @field:SerializedName("recoveryDate")
    var recoveryDate: String? = "",

    @field:SerializedName("ptpAmountType")
    var ptpAmountType: String? = "",

    @field:SerializedName("ptpDate")
    var ptpDate: String? = "",

    @field:SerializedName("recoveryType")
    var recoveryType: String? = "",

    @field:SerializedName("assignTracer")
    var assignTracer: Boolean? = false,

    @field:SerializedName("ptpProbability")
    var ptpProbability: String? = ""
)
