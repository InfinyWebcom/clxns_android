package com.clxns.app.utils

import com.clxns.app.BuildConfig

class Constants {
    companion object {
        const val PREF_NAME = "clxns_pref_${BuildConfig.APPLICATION_ID}"
        const val TOKEN = "token"
        const val USER_NAME = "user_name"
        const val USER_MOBILE = "user_mobile"
        const val USER_EMAIL = "user_email"
        const val USER_ADDRESS = "user_address"
        const val USER_LOCATION = "user_location"
        const val USER_DOB = "user_dob"
        const val USER_ID = "user_dob"
        const val IS_USER_LOGGED_IN = "logged_in_status"
        const val SPLASH_SCREEN_TIMEOUT: Long = 1000
    }
}