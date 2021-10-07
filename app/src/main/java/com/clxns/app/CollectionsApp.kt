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
         * Home Retry button was not clickable - Fixed
         * Crashlytics working now
         * Reset btn will be shown if user applies filter from cases screen
         * Client Feedback Issue - Resolved
         */
    }
}