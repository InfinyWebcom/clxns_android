package com.clxns.app.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserData(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val emergencyPhone: String,
    val dob: String,
    val address: String,
    val location: String,
    val bloodGroup: String,
    val roleId: Int,
    val reportingTo: Int,
    val experience: Int,
    @Json(name = "pincode")
    val pinCode: Int,
    val employeeId: String,
    val language: String,
    val otp: String?,
    val otpExpired: String?,
    @Json(name = "profileImg")
    val profileImage: String?,
    @Json(name = "reporting")
    val reportingDetails: ReportingData?
) {
    @JsonClass(generateAdapter = true)
    data class ReportingData(
        val id: Int,
        val firstName: String,
        val lastName: String,
        val email: String,
        val phone: String
    )
}