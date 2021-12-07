package com.clxns.app.data.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdditionalFieldModel(

    @field:SerializedName("recoveredAmount")
    var Recovered_Amount: String? = "",

    @field:SerializedName("refChequeNo")
    var RefChequeNo: String? = "",

    @field:SerializedName("paymentMode")
    var Payment_Mode: String? = "",

    @field:SerializedName("ptpAmount")
    var PTP_Amount: String? = "",

    @field:SerializedName("recoveryDate")
    var Recovery_Date: String? = "",

    @field:SerializedName("ptpAmountType")
    var PTP_Amount_Type: String? = "",

    @field:SerializedName("ptpDate")
    var PTP_Date: String? = "",

    @field:SerializedName("recoveryType")
    var Recovery_Type: String? = "",

    @field:SerializedName("assignTracer")
    var Assign_To_Tracer: Boolean? = false,

    @field:SerializedName("ptpProbability")
    var PTP_Probability: String? = ""
)
