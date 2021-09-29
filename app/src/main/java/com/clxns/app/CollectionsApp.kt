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
         * Show current applied filter status in the cases screen - TODO
         * Replace progress bar with Shimmer layout - TODO
         * Home Screen & Profile Redesign UI - TODO
         * Case Summary Bottom Sheet - Convert Static UI to Dynamic using Local Dispositions data & RV - TODO
         * Make CasesFilter Bottom Sheet to Persistent Bottom Sheet - TODO
         */
    }
}