package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.CasesResponse
import com.clxns.app.data.model.ForgotPasswordResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@ActivityRetainedScoped
class ForgotPasswordRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun getOTP(token: String): Flow<NetworkResult<ForgotPasswordResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getOTP(token) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifyOTP(
        token: String,
        otp: String,
        mobileNo: String
    ): Flow<NetworkResult<ForgotPasswordResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.verifyOTP(token, otp, mobileNo) })
        }.flowOn(Dispatchers.IO)
    }

//    suspend fun getOTP(mobileNo: String) : Response<ForgotPasswordResponse>{
//        return apiService.getOTP(mobileNo)
//    }
//
//    suspend fun verifyOTP(token:String, otp:String, mobileNo: String) : Response<ForgotPasswordResponse>{
//        return apiService.verifyOTP(token, otp, mobileNo)
//    }
}