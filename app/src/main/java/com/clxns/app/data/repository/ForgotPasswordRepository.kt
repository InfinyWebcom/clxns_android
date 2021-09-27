package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.ForgotPasswordResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class ForgotPasswordRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun getOTP(token: String): Flow<NetworkResult<ForgotPasswordResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getOTP(token) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifyOTP(
        token: String,
        otp: String,
        emailId: String
    ): Flow<NetworkResult<ForgotPasswordResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.verifyOTP(token, otp, emailId) })
        }.flowOn(Dispatchers.IO)
    }

}