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
         * In the listing, unable to see the images of the bank.
         * Go to "This Month" tab, it shows some data in all the fields. Now pull down to refresh on this screen, all the data turns 0.
         * Added corner radius to check in bottom sheet
         * Only Portrait support for the remaining screens
         * Check In Screen Progress bar was too small to be seen - FIXED
         * Changed Check In & Payment Collection Date Format
         */
    }
}