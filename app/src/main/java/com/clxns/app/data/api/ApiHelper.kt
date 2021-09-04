package com.clxns.app.data.api

import com.clxns.app.data.model.ForgotPasswordResponse
import com.clxns.app.data.model.LoginResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun performLogin(mobileNo : String, password : String) : Response<LoginResponse>

    suspend fun getOTP(mobileNo: String) : Response<ForgotPasswordResponse>

    suspend fun verifyOTP(token:String, otp:String, mobileNo:String) : Response<ForgotPasswordResponse>
}