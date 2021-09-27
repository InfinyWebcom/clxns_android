package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.HomeStatisticsResponse
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

    suspend fun getCaseDetails(token: String,loanAccountNumber: String): Flow<NetworkResult<CaseDetailsResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getCaseDetails(token,loanAccountNumber) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addPayment(
        token: String,
        leadId: String,
        loanNo: String,
        amtType: String,
        paymentMode: String,
        recoveryDate: String,
        refNo: String,
        chequeNo: String,
        remark: String,
        supporting:  List<String>
    ): Flow<NetworkResult<HomeStatisticsResponse>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.addPayment(
                    token, leadId, loanNo, amtType,
                    paymentMode, recoveryDate, refNo, chequeNo, remark, supporting
                )
            })
        }.flowOn(Dispatchers.IO)
    }
}