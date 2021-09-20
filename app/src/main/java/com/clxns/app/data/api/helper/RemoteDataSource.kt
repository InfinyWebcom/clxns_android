package com.clxns.app.data.api.helper

import com.clxns.app.data.api.ApiService
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun performLogin(emailId: String, password: String) =
        apiService.performLogin(emailId, password)

    suspend fun logout(token: String) =
        apiService.logout(token)

    suspend fun getOTP(emailId: String) =
        apiService.getOTP(emailId)

    suspend fun verifyOTP(
        token: String,
        otp: String,
        emailId: String
    ) = apiService.verifyOTP(token, otp, emailId)

    suspend fun changePassword(
        token: String,
        newPassword: String,
        confirmPassword: String,
        oldPassword: String
    ) = apiService.changePassword(token, newPassword, confirmPassword, oldPassword)

    suspend fun getCasesList(token: String) =
        apiService.getCasesList(token, "", 10, 0, "", "")

    suspend fun getMyPlanList(token: String) =
        apiService.getMyPlanList(token)

    suspend fun getCaseDetails(token: String, loanAccountNo: String) =
        apiService.getCaseDetails(token, loanAccountNo)

    suspend fun caseHistory(token: String, loanAccountNo: String) =
        apiService.caseHistory(token, loanAccountNo)
}

