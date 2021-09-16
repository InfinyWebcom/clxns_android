package com.clxns.app.data.api

import com.clxns.app.data.model.CasesResponse
import com.clxns.app.data.model.ChangePasswordResponse
import com.clxns.app.data.model.ForgotPasswordResponse
import com.clxns.app.data.model.LoginResponse
import retrofit2.Response

interface ApiHelper {
    suspend fun performLogin(emailId: String, password: String): Response<LoginResponse>

    suspend fun getOTP(emailId: String): Response<ForgotPasswordResponse>

    suspend fun verifyOTP(
        token: String,
        otp: String,
        emailId: String
    ): Response<ForgotPasswordResponse>

    suspend fun changePassword(
        token: String,
        newPassword: String,
        confirmPassword: String,
        oldPassword: String
    ): Response<ChangePasswordResponse>

    suspend fun getCasesList(token: String) : Response<CasesResponse>
}