package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.api.helper.BaseApiResponse
import com.clxns.app.data.api.helper.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import javax.inject.Singleton

@ActivityRetainedScoped
class HomeRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {
}