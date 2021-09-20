package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.LoginResponse
import com.clxns.app.data.model.LogoutResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ActivityRetainedScoped
class ProfileRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun logout(token: String): Flow<NetworkResult<LogoutResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.logout(token) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserDetails(token: String) : Flow<NetworkResult<LoginResponse>>{
        return flow {
            emit(safeApiCall { remoteDataSource.getUserDetails(token) })
        }.flowOn(Dispatchers.IO)
    }
}