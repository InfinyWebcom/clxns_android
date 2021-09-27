package com.clxns.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class CollectionsApp : Application(){

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
        /**
         * Add OTP Timer in Forgot Password Activity - TODO
         * Show current applied filter in the cases screen - TODO
         * Replace progress bar with Shimmer layout - TODO
         */
    }
}