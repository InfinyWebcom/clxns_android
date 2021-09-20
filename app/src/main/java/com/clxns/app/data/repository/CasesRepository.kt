package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.AddToPlanModel
import com.clxns.app.data.model.CasesResponse
import com.clxns.app.data.model.LoginResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@ActivityRetainedScoped
class CasesRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun getCasesList(token: String): Flow<NetworkResult<CasesResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getCasesList(token) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addToPlan(
        token: String,
        leadId: String,
        planDate: String
    ): Flow<NetworkResult<AddToPlanModel>> {
        return flow {
            emit(safeApiCall { remoteDataSource.addToPlan(token, leadId, planDate) })
        }.flowOn(Dispatchers.IO)
    }
}