package com.clxns.app.data.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentModel(

    @field:SerializedName("leadId")
    var leadId: String? = "",

    @field:SerializedName("loanNo")
    var loanNo: String? = "",

    @field:SerializedName("amtType")
    var amtType: String? = "",

    @field:SerializedName("paymentMode")
    var paymentMode: String? = "",

    @field:SerializedName("recoveryDate")
    var recoveryDate: String? = "",

    @field:SerializedName("refNo")
    var refNo: String? = "",

    @field:SerializedName("chequeNo")
    var chequeNo: String? = "",

    @field:SerializedName("remark")
    var remark: String? = "",

    @field:SerializedName("supporting")
    var supporting: List<String>? = null,

    @field:SerializedName("collectedAmt")
    var collectedAmt: Long? = 0
)
