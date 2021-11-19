package com.clxns.app.data.api.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.clxns.app.utils.NoInternetException
import com.clxns.app.utils.ViewUtils
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor(
    context: Context
) : Interceptor {

    private val applicationContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!ViewUtils.isInternetAvailable(applicationContext))
            throw NoInternetException("Make sure you have an active data connection")
        return chain.proceed(chain.request())
    }
}