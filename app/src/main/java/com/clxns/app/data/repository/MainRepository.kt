package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.database.BankDetailsEntity
import com.clxns.app.data.database.DispositionEntity
import com.clxns.app.data.database.LocalDataSource
import com.clxns.app.data.database.SubDispositionEntity
import com.clxns.app.data.model.DispositionResponse
import com.clxns.app.data.model.FISBankResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class MainRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : BaseApiResponse() {

    suspend fun getAllDispositions(): Flow<NetworkResult<DispositionResponse>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.getAllDispositions()
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getBankList(token: String): Flow<NetworkResult<FISBankResponse>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.getBankList(token)
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun saveAllDispositions(dispositionList: List<DispositionEntity>) =
        localDataSource.saveAllDispositions(dispositionList)

    suspend fun saveAllSubDispositions(subDispositionList: List<SubDispositionEntity>) =
        localDataSource.saveAllSubDispositions(subDispositionList)

    suspend fun saveAllBankDetails(bankDetailList: List<BankDetailsEntity>) =
        localDataSource.saveAllBankDetails(bankDetailList)
}