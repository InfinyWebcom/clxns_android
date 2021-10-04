package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.database.LocalDataSource
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.LeadContactUpdateResponse
import com.clxns.app.data.model.MyPlanModel
import com.clxns.app.data.model.cases.CaseCheckInBody
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


@ActivityRetainedScoped
class CheckInRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : BaseApiResponse() {


//    suspend fun saveCheckInData(
//        token: String,
//        loanAccountNo: String,
//        dispositionId: String,
//        subDispositionId: String?,
//        comments: String,
//        followUp: String,
//        nextAction: String,
//        additionalField: String,
//        location: String,
//        supporting: List<String>,
//        payment: String
//    ): Flow<NetworkResult<MyPlanModel>> {
//        return flow {
//            emit(safeApiCall {
//                remoteDataSource.saveCheckInData(
//                    token, loanAccountNo, dispositionId, subDispositionId,
//                    comments, followUp, nextAction, additionalField, location, supporting,payment
//                )
//            })
//        }.flowOn(Dispatchers.IO)
//    }

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

    suspend fun leadContactUpdate(
        token: String,
        leadId: String,
        type: String,
        content: String
    ): Flow<NetworkResult<LeadContactUpdateResponse>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.leadContactUpdate(
                    token, leadId, type, content
                )
            })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCaseDetails(
        token: String,
        loanAccountNumber: String
    ): Flow<NetworkResult<CaseDetailsResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getCaseDetails(token, loanAccountNumber) })
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