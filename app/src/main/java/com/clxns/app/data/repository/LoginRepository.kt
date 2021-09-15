package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.model.LoginResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val apiService: ApiService
){

    suspend fun performLogin(emailId: String, password: String) : Response<LoginResponse>?{
         return apiService.performLogin(emailId, password)
    }
}