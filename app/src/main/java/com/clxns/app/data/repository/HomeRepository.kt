package com.clxns.app.data.repository

import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.api.helper.RemoteDataSource
import com.clxns.app.data.model.home.HomeStatisticsResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@ActivityRetainedScoped
class HomeRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {
    suspend fun getHomeStatsData(token: String): Flow<NetworkResult<HomeStatisticsResponse>> {
        return flow {
            emit(safeApiCall {
                remoteDataSource.getHomeStatsData(token)
            })
        }.flowOn(Dispatchers.IO)
    }
}
