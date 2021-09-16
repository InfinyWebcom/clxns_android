package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import com.clxns.app.data.model.CasesResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CasesRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getCasesList(token :String): Response<CasesResponse> {
        return apiService.getCasesList(token, "",10,0,"","")
    }
}