package com.clxns.app.data.api.helper

import android.content.Context
import com.clxns.app.utils.NoInternetException
import com.clxns.app.utils.ViewUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Network Interceptor that is being used by Retrofit Http Client, and this class is responsible for performing the network call
 */
class NetworkConnectionInterceptor(
    context: Context
) : Interceptor {

    private val applicationContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!ViewUtils.isInternetAvailable(applicationContext))
            throw NoInternetException("Check if you have active data connection OR wifi connected to network")
        return chain.proceed(chain.request())
    }
}