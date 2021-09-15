package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.model.ChangePasswordResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChangePasswordRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun changePassword(
        token: String,
        newPassword: String,
        confirmPassword: String,
        oldPassword: String
    ) : Response<ChangePasswordResponse>{
        return apiService.changePassword(token, newPassword, confirmPassword, oldPassword)
    }
}