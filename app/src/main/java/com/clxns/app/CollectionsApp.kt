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
            /**
             * On Collect & Partial Collect if we tap more than one time it opens payment activity but also gets checked in with the Collected Disposition - FIXED
             * Wrote a function to handle the fast double tap for Button or Clickable elements
             * UI Clean up & Documentation
             */
        }
    }
}