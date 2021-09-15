package com.clxns.app.data.api

import com.clxns.app.data.model.ChangePasswordResponse
import com.clxns.app.data.model.ForgotPasswordResponse
import com.clxns.app.data.model.LoginResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override suspend fun performLogin(emailId: String, password: String): Response<LoginResponse> =
        apiService.performLogin(emailId, password)

    override suspend fun getOTP(emailId: String): Response<ForgotPasswordResponse> =
        apiService.getOTP(emailId)

    override suspend fun verifyOTP(
        token: String,
        otp: String,
        emailId: String
    ): Response<ForgotPasswordResponse> =
        apiService.verifyOTP(token, otp, emailId)

    override suspend fun changePassword(
        token: String,
        newPassword: String,
        confirmPassword: String,
        oldPassword: String
    ): Response<ChangePasswordResponse> =
        apiService.changePassword(token, newPassword, confirmPassword, oldPassword)

}
