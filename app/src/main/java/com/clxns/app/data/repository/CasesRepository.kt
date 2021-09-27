package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.database.LocalDataSource
import com.clxns.app.data.model.AddToPlanModel
import com.clxns.app.data.model.cases.CasesResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class CasesRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : BaseApiResponse() {

    //Network Calls
    suspend fun getCasesList(
        token: String,
        searchTxt: String,
        dispositionId: String,
        subDispositionId: String,
        fromDate: String,
        toDate: String
    ): Flow<NetworkResult<CasesResponse>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.getCasesList(
                    token,
                    searchTxt,
                    dispositionId,
                    subDispositionId,
                    fromDate,
                    toDate
                )
            })
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

    //Local Calls
    suspend fun getAllDispositionsFromRoomDB(): Flow<List<String>> {
        return flow {
            emit(localDataSource.getAllDispositionsFromRoomDB())
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllSubDispositionsFromRoomDB(dispositionId: Int): Flow<List<String>> {
        return flow {
            emit(localDataSource.getAllSubDispositionsFromRoomDB(dispositionId))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDispositionIdFromRoomDB(dispositionName: String): Flow<Int> {
        return flow {
            emit(localDataSource.getDispositionId(dispositionName))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getSubDispositionIdFromRoomDB(subDispositionName: String): Flow<Int> {
        return flow {
            emit(localDataSource.getSubDispositionId(subDispositionName))
        }.flowOn(Dispatchers.IO)
    }
}