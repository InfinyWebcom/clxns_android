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
             * Plan list was refreshing after successful payment to current date only even if the selected date was different - FIXED
             * Dashboard issues - Recovery amount for today, summary data, from summary bottom sheet any disposition click from today tab was not showing in the cases list - RESOLVED
             * Total cases button in the summary bottom sheet - HIDDEN
             * Map Fragment Infinite loading problem ( May be due to no internet although it works without the internet just takes more time to fetch gps coordinates using sensors) - FIXED
             * New Changes from the client - DONE
             */
        }
    }
}