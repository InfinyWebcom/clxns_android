package com.clxns.app.data.api

import com.clxns.app.data.model.ForgotPasswordResponse
import com.clxns.app.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("fos/login")
    @FormUrlEncoded
    suspend fun performLogin(
        @Field("mobile") mobileNo: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("fos/forgotPassword")
    @FormUrlEncoded
    suspend fun getOTP(
        @Field("mobile") mobileNo: String
    ) : Response<ForgotPasswordResponse>


    @POST("fos/verifyPassword")
    @FormUrlEncoded
    suspend fun verifyOTP(
        @Field("token") token : String,
        @Field("verifyOtp") otp : String,
        @Field("mobile") mobileNo: String
    ) : Response<ForgotPasswordResponse>


}