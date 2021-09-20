package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.CaseHistoryResponse
import com.clxns.app.data.model.Lead
import com.clxns.app.data.model.MyPlanModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class HistoryRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun getCaseHistory(token: String,loanAccountNumber: String): Flow<NetworkResult<CaseHistoryResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getCaseHistory(token,loanAccountNumber) })
        }.flowOn(Dispatchers.IO)
    }

}