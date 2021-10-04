package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.database.LocalDataSource
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.Lead
import com.clxns.app.data.model.MyPlanModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class DetailsRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : BaseApiResponse() {

    suspend fun getCaseDetails(token: String,loanAccountNumber: String): Flow<NetworkResult<CaseDetailsResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getCaseDetails(token,loanAccountNumber) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDispositionName(dispositionId:Int) : Flow<String>{
        return flow {
            emit(localDataSource.getDispositionName(dispositionId))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getSubDispositionName(subDispositionId:Int) : Flow<String>{
        return flow {
            emit(localDataSource.getSubDispositionName(subDispositionId))
        }.flowOn(Dispatchers.IO)
    }

}