package com.clxns.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CollectionsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        /**
         * Add OTP Timer in Forgot Password Activity - TODO
         * Crash on History Screen due to jsonObjectException - Fixed
         * Crash on History link click from Detail Screen - Fixed
         */
    }
}