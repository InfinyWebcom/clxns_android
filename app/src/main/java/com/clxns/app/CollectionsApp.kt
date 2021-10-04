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
         * Without email and password directly if I click on Login button then validation error should display for the fields
         * After checkin status is not getting changed real time, need to click on some other tab and again if i click on My plan then able to see updated status
         * Pull down to refresh missing in the home page.
         * Replaced arrow with new arrow
         * Added Total Due amount minus collectd amount in Cases & My Plan, History Screen
         * Refresh my plan list & detail screen on check in updates
         * Different disposition & sub disposition issue - FIXED
         * PTP Probability Issue & Crashing - FIXED
         * Ref No. & amount in History screen
         * Detail Screen & MY Plan on updating after payment - Fixed
         */
    }
}