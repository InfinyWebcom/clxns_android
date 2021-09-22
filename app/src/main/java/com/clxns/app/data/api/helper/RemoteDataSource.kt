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

    suspend fun getAllDispositions() = apiService.getAllDispositions()

    suspend fun getCasesList(token: String, searchTxt:String) =
        apiService.getCasesList(token, searchTxt, 10, 0, "", "")

    suspend fun getMyPlanList(token: String) =
        apiService.getMyPlanList(token)

    suspend fun getCaseDetails(token: String, loanAccountNumber: String) =
        apiService.getCaseDetails(token, loanAccountNumber)

    suspend fun getCaseHistory(token: String, loanAccountNumber: String) =
        apiService.getCaseHistory(token, loanAccountNumber)

    suspend fun getUserDetails(token: String) = apiService.getUserDetails(token)

    suspend fun saveCheckInData(
        token: String,
        loanAccountNo: String,
        dispositionId: String,
        subDispositionId: String,
        comments: String,
        file: String,
        followUp: String,
        nextAction: String,
        additionalField: String
    ) = apiService.saveCheckInData(
        token, loanAccountNo, dispositionId, subDispositionId,
        comments, file, followUp, nextAction, additionalField
    )

    suspend fun addToPlan(token: String, leadId: String, planDate: String) =
        apiService.addToPlan(token, leadId, planDate)

    suspend fun getHomeStatsData(token: String) = apiService.getHomeStatsData(token)

}


