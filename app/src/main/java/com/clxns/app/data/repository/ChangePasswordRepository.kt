package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.CasesResponse
import com.clxns.app.data.model.ChangePasswordResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@ActivityRetainedScoped
class ChangePasswordRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun changePassword(
        token: String,
        newPassword: String,
        confirmPassword: String,
        oldPassword: String
    ): Flow<NetworkResult<ChangePasswordResponse>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.changePassword(
                    token,
                    newPassword,
                    confirmPassword,
                    oldPassword
                )
            })
        }.flowOn(Dispatchers.IO)
    }

//    suspend fun changePassword(
//        token: String,
//        newPassword: String,
//        confirmPassword: String,
//        oldPassword: String
//    ): Response<ChangePasswordResponse> {
//        return apiService.changePassword(token, newPassword, confirmPassword, oldPassword)
//    }
}