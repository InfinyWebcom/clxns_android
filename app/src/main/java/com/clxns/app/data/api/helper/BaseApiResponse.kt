package com.clxns.app.data.api.helper

import retrofit2.Response

/**
 * Base Class for performing network call which handles the exceptions smoothly, removing the need of
 * handling base cases such as checking if the network call was successful or not.
 * Combination of this and NetworkConnectionInterceptor we are also throwing no internet exception with a proper message
 */
abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(body)
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
        NetworkResult.Error("Error -> $errorMessage")
}