package com.clxns.app.data.repository

import com.clxns.app.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {
}