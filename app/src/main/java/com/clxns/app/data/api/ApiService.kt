package com.clxns.app.data.api

import com.clxns.app.data.model.*
import com.clxns.app.data.model.cases.CaseCheckInBody
import com.clxns.app.data.model.cases.CasesResponse
import com.clxns.app.data.model.home.HomeStatisticsResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("fos/login")
    @FormUrlEncoded
    suspend fun performLogin(
        @Field("email") emailId: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("fos/logout")
    suspend fun logout(
        @Header("token") token: String
    ): Response<LogoutResponse>

    @POST("fos/forgotPassword")
    @FormUrlEncoded
    suspend fun getOTP(
        @Field("email") emailId: String
    ): Response<ForgotPasswordResponse>


    @POST("fos/changePassword")
    @FormUrlEncoded
    suspend fun changePassword(
        @Header("token") token: String,
        @Field("password") newPassword: String,
        @Field("confirmPassword") confirmPassword: String,
        @Field("oldPassword") oldPassword: String
    ): Response<ChangePasswordResponse>


    @POST("fos/verifyOTP")
    @FormUrlEncoded
    suspend fun verifyOTP(
        @Field("token") token: String,
        @Field("verifyOtp") otp: String,
        @Field("email") emailId: String
    ): Response<ForgotPasswordResponse>

    @POST("fos/getCasesList")
    @FormUrlEncoded
    suspend fun getCasesList(
        @Header("token") token: String,
        @Field("search") searchTxt: String,
        @Field("start") start: Int,
        @Field("dispositionId") dispositionId: String,
        @Field("subdispositionId") subDispositionId: String,
        @Field("fromDate") fromDate: String,
        @Field("toDate") toDate: String,
        @Field("viewPending") visitPending: String,
        @Field("followUp") followUp: String
    ): Response<CasesResponse>


    @POST("fos/listMyPlan")
    @FormUrlEncoded
    suspend fun getMyPlanList(
        @Header("token") token: String,
        @Field("planDate") planDate: String
    ): Response<MyPlanModel>

    @POST("fos/listFosDis")

    suspend fun getAllDispositions(): Response<DispositionResponse>

    @FormUrlEncoded
    @POST("fos/getCaseDetails")
    suspend fun getCaseDetails(
        @Header("token") token: String,
        @Field("loanAccountNo") loanAccountNo: String
    ): Response<CaseDetailsResponse>

    @FormUrlEncoded
    @POST("fos/caseHistory")
    suspend fun getCaseHistory(
        @Header("token") token: String,
        @Field("loanAccountNo") loanAccountNo: String
    ): Response<HistoryResponse>

    @POST("fos/saveCheckinData")
    suspend fun saveCheckInData(
        @Header("token") token: String,
        @Body() body: CaseCheckInBody
    ): Response<MyPlanModel>

    @POST("fos/getUserDetails")
    suspend fun getUserDetails(@Header("token") token: String): Response<LoginResponse>

    @FormUrlEncoded
    @POST("fos/addToPlan")
    suspend fun addToPlan(
        @Header("token") token: String,
        @Field("leadId") leadId: String,
        @Field("planDate") planDate: String
    ): Response<AddToPlanModel>

    @POST("fos/getStatisticsData")
    suspend fun getHomeStatsData(@Header("token") token: String): Response<HomeStatisticsResponse>

    @POST("fos/listBank")
    suspend fun getBankList(@Header("token") token: String): Response<FISBankResponse>

    @FormUrlEncoded
    @POST("fos/leadContactUpdate")
    suspend fun leadContactUpdate(
        @Header("token") token: String,
        @Field("leadId") leadId: String,
        @Field("type") type: String,
        @Field("content") content: String
    ): Response<LeadContactUpdateResponse>

    @POST("fos/deletePlan")
    @FormUrlEncoded
    suspend fun removePlan(
        @Header("token") token: String,
        @Field("leadId") leadId: String
    ): Response<UnPlanResponse>

}