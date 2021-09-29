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
         * Show current applied filter in the cases screen - TODO
         * Replace progress bar with Shimmer layout - TODO
         * Home Screen & Profile Redesign UI - TODO
         * Case Summary Bottom Sheet - Convert Static UI to Dynamic using Local Dispositions data & RV - TODO
         * Make CasesFilter Bottom Sheet to Persistent Bottom Sheet - TODO
         * My plan filter issue - Fixed
         * Dispositions in the my plan screen
         * Home Summary bottom sheet to cases filter with disposition filter navigation - DONE
         * Added Cases & My Plan Icon
         * Updating Cases, Search Cases & My Plan screen accordingly on the update change from
         * the detail screen & search cases screen if the user changes the plan status
         * Crashing due to scrolling while updating the data in Cases & Search Cases screen - FIXED
         * My plan only max 5 lead were showing - Fixed (Pagination removed from the backend)
         */
    }
}