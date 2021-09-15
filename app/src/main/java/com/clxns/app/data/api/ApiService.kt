package com.clxns.app.data.api

import com.clxns.app.data.model.ChangePasswordResponse
import com.clxns.app.data.model.ForgotPasswordResponse
import com.clxns.app.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("fos/login")
    @FormUrlEncoded
    suspend fun performLogin(
        @Field("email") emailId: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("fos/forgotPassword")
    @FormUrlEncoded
    suspend fun getOTP(
        @Field("email") emailId: String
    ) : Response<ForgotPasswordResponse>


    @POST("fos/changePassword")
    @FormUrlEncoded
    suspend fun changePassword(
        @Header("token") token: String,
        @Field("password") newPassword: String,
        @Field("confirmPassword") confirmPassword: String,
        @Field("oldPassword") oldPassword: String
    ) : Response<ChangePasswordResponse>


    @POST("fos/verifyOTP")
    @FormUrlEncoded
    suspend fun verifyOTP(
        @Field("token") token : String,
        @Field("verifyOtp") otp : String,
        @Field("email") emailId: String
    ) : Response<ForgotPasswordResponse>


}