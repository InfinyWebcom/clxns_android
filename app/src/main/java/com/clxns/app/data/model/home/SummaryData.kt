package com.clxns.app.data.model.home

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SummaryData(
    @Json(name = "brokenPTP")
    val brokenPTP : Int,
    @Json(name = "callBack")
    val callBack : Int,
    @Json(name = "collect")
    val collect : Int,
    @Json(name = "customerDeceased")
    val customerDeceased : Int,
    @Json(name = "customerNotFound")
    val customerNotFound : Int,
    @Json(name = "denialRTP")
    val denialRTP : Int,
    @Json(name = "dispute")
    val dispute : Int,
    @Json(name = "partiallyCollect")
    val partiallyCollect : Int,
    @Json(name = "promiseToPay")
    val promiseToPay : Int,
    @Json(name = "settlementForeclosure")
    val settlementForeclosure : Int,
    @Json(name = "totalCases")
    val totalCases : Int
) : Parcelable {
    constructor(parcel : Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel : Parcel, flags : Int) {
        parcel.writeInt(brokenPTP)
        parcel.writeInt(callBack)
        parcel.writeInt(collect)
        parcel.writeInt(customerDeceased)
        parcel.writeInt(customerNotFound)
        parcel.writeInt(denialRTP)
        parcel.writeInt(dispute)
        parcel.writeInt(partiallyCollect)
        parcel.writeInt(promiseToPay)
        parcel.writeInt(settlementForeclosure)
        parcel.writeInt(totalCases)
    }

    override fun describeContents() : Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SummaryData> {
        override fun createFromParcel(parcel : Parcel) : SummaryData {
            return SummaryData(parcel)
        }

        override fun newArray(size : Int) : Array<SummaryData?> {
            return arrayOfNulls(size)
        }
    }
}