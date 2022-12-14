package com.clxns.app.data.api.helper

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.model.cases.CaseCheckInBody
import javax.inject.Inject

/**
 * Remote Data Source class with all the suspend function which can only be triggered using Coroutine on the independent background thread
 * @param apiService Rest Api Implementation with all the endpoints required for this project
 */
class RemoteDataSource @Inject constructor(private val apiService : ApiService) {

    suspend fun performLogin(emailId : String, password : String) =
        apiService.performLogin(emailId, password)

    suspend fun logout(token : String) =
        apiService.logout(token)

    suspend fun getOTP(emailId : String) =
        apiService.getOTP(emailId)

    suspend fun verifyOTP(
        token : String,
        otp : String,
        emailId : String
    ) = apiService.verifyOTP(token, otp, emailId)

    suspend fun changePassword(
        token : String,
        newPassword : String,
        confirmPassword : String,
        oldPassword : String
    ) = apiService.changePassword(token, newPassword, confirmPassword, oldPassword)

    suspend fun getAllDispositions() = apiService.getAllDispositions()

    suspend fun getCasesList(
        token : String,
        searchTxt : String,
        dispositionId : String,
        subDispositionId : String,
        fromDate : String,
        toDate : String,
        visitPending : String,
        followUp : String
    ) =
        apiService.getCasesList(
            token,
            searchTxt,
            0,
            dispositionId,
            subDispositionId,
            fromDate,
            toDate,
            visitPending,
            followUp
        )

    suspend fun getMyPlanList(token : String, planDate : String) =
        apiService.getMyPlanList(token, planDate)

    suspend fun getCaseDetails(token : String, loanAccountNumber : String) =
        apiService.getCaseDetails(token, loanAccountNumber)

    suspend fun getCaseHistory(token : String, loanAccountNumber : String) =
        apiService.getCaseHistory(token, loanAccountNumber)

    suspend fun getUserDetails(token : String) = apiService.getUserDetails(token)

    suspend fun saveCheckInData(
        token : String,
        body : CaseCheckInBody
    ) = apiService.saveCheckInData(
        token, body
    )

    suspend fun addToPlan(token : String, leadId : String, planDate : String) =
        apiService.addToPlan(token, leadId, planDate)

    suspend fun getHomeStatsData(token : String) = apiService.getHomeStatsData(token)

    suspend fun getBankList(token : String) = apiService.getBankList(token)

    suspend fun leadContactUpdate(
        token : String,
        leadId : String,
        type : String,
        content : String
    ) = apiService.leadContactUpdate(
        token, leadId, type, content
    )

    suspend fun removePlan(
        token : String,
        leadId : String
    ) = apiService.removePlan(token, leadId)

}