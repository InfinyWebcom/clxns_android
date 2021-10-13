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
         * After we un plan a case from the detail screen, the CheckIn Button should get hide - DONE
         * Case Filter had an issue when we apply filter it recreates the cases fragment because
         * when you click back button it doesn't go back to home screen instead goes back to previous
         * instance of cases screen - FIXED
         * Case Filter Reset Button will clear the field, as we apply filter then we can see the reset button
         * on the top for resetting the filters.
         */
    }
}