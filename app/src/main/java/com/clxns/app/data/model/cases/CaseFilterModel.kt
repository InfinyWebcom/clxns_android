package com.clxns.app.data.model.cases

import android.os.Parcel
import android.os.Parcelable

class CaseFilterModel() : Parcelable {
    var dispositionId : String = ""
    var visitPending : String = ""
    var followUp : String = ""
    var fromDate : String = ""
    var toDate : String = ""

    constructor(parcel : Parcel) : this() {
        dispositionId = parcel.readString().toString()
        visitPending = parcel.readString().toString()
        followUp = parcel.readString().toString()
        fromDate = parcel.readString().toString()
        toDate = parcel.readString().toString()
    }

    override fun writeToParcel(parcel : Parcel, flags : Int) {
        parcel.writeString(dispositionId)
        parcel.writeString(visitPending)
        parcel.writeString(followUp)
        parcel.writeString(fromDate)
        parcel.writeString(toDate)
    }

    override fun describeContents() : Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CaseFilterModel> {
        override fun createFromParcel(parcel : Parcel) : CaseFilterModel {
            return CaseFilterModel(parcel)
        }

        override fun newArray(size : Int) : Array<CaseFilterModel?> {
            return arrayOfNulls(size)
        }
    }
}