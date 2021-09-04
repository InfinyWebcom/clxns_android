package com.clxns.app.data.api

import com.clxns.app.data.model.ForgotPasswordResponse
import com.clxns.app.data.model.LoginResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override suspend fun performLogin(mobileNo: String, password: String): Response<LoginResponse> =
        apiService.performLogin(mobileNo, password)

    override suspend fun getOTP(mobileNo: String): Response<ForgotPasswordResponse> =
        apiService.getOTP(mobileNo)

    override suspend fun verifyOTP(
        token: String,
        otp: String,
        mobileNo: String
    ): Response<ForgotPasswordResponse> =
        apiService.verifyOTP(token, otp, mobileNo)
}
