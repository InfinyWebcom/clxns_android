package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.MyPlanModel
import com.clxns.app.data.model.cases.CaseCheckInBody
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class PaymentCollectionRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun saveCheckInData(
        token: String,
        body: CaseCheckInBody
    ): Flow<NetworkResult<MyPlanModel>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.saveCheckInData(
                    token, body
                )
            })
        }.flowOn(Dispatchers.IO)
    }
}