package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val apiService: ApiService
) {
}