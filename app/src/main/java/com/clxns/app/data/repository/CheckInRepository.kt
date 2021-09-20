package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.CasesResponse
import com.clxns.app.data.model.MyPlanModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class CheckInRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {


    suspend fun saveCheckInData(
        token: String,
        loanAccountNo: String,
        dispositionId: String,
        subDispositionId: String,
        comments: String,
        file: String,
        followUp: String,
        nextAction: String,
        additionalField: String
    ): Flow<NetworkResult<MyPlanModel>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.saveCheckInData(
                    token, loanAccountNo, dispositionId, subDispositionId,
                    comments, file, followUp, nextAction, additionalField
                )
            })
        }.flowOn(Dispatchers.IO)
    }
}