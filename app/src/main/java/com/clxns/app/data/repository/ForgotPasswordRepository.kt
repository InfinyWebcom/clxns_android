package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.model.ForgotPasswordResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForgotPasswordRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getOTP(mobileNo: String) : Response<ForgotPasswordResponse>{
        return apiService.getOTP(mobileNo)
    }

    suspend fun verifyOTP(token:String, otp:String, mobileNo: String) : Response<ForgotPasswordResponse>{
        return apiService.verifyOTP(token, otp, mobileNo)
    }
}