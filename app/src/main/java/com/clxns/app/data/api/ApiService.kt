package com.clxns.app.data.api

import com.clxns.app.data.model.*
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.*

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
        @Field("filter") filter: String,
        @Field("length") length: Int,
        @Field("start") start: Int,
        @Field("dispositionId") dispositionId: String,
        @Field("subdispositionId") subDispositionId: String
    ): Response<CasesResponse>


    @POST("fos/listMyPlan")
    suspend fun getMyPlanList(
        @Header("token") token: String
    ): Response<MyPlanModel>
    
         @POST("fos/listFosDis")

     suspend fun getAllDispositions() : Response<DispositionResponse>

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
    ): Response<CaseHistoryResponse>

    @FormUrlEncoded
    @POST("fos/saveCheckinData")
    suspend fun saveCheckInData(
        @Header("token") token: String,
        @Field("loanAccountNo") loanAccountNo: String,
        @Field("dispositionId") dispositionId: String,
        @Field("subDispositionId") subDispositionId: String,
        @Field("comments") comments: String,
        @Field("file") file: String,
        @Field("followUp") followUp: String,
        @Field("nextAction") nextAction: String,
        @Field("additionalField") additionalField: String
    ): Response<MyPlanModel>

}