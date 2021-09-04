package com.clxns.app.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "data")
    val loginData: LoginData? = null,
    @Json(name = "error")
    val error: Boolean,
    @Json(name = "title")
    val title: String,
    @Json(name = "token")
    val token: String = "",
    val flag: String = "",

)

@JsonClass(generateAdapter = true)
data class LoginData(
    @Json(name = "address")
    val address: String,
    @Json(name = "bloodGroup")
    val bloodGroup: String,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "dob")
    val dob: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "emergencyPhone")
    val emergencyPhone: String,
    @Json(name = "employeeId")
    val employeeId: String,
    @Json(name = "experience")
    val experience: Int,
    @Json(name = "firstName")
    val firstName: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "isDeleted")
    val isDeleted: Int,
    @Json(name = "isEmailVerified")
    val isEmailVerified: Int,
    @Json(name = "language")
    val language: String,
    @Json(name = "lastName")
    val lastName: String,
    @Json(name = "latitude")
    val latitude: Int,
    @Json(name = "location")
    val location: String,
    @Json(name = "longitude")
    val longitude: Int,
    @Json(name = "otp")
    val otp: Any?,
    @Json(name = "otpExpired")
    val otpExpired: Any?,
    @Json(name = "password")
    val password: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "pincode")
    val pincode: Int,
    @Json(name = "reportingTo")
    val reportingTo: Int,
    @Json(name = "roleId")
    val roleId: Int,
    @Json(name = "tempCode")
    val tempCode: String,
    @Json(name = "updatedAt")
    val updatedAt: String
)