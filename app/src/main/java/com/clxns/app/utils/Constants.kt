package com.clxns.app.utils

import android.text.TextUtils
import android.util.Patterns
import com.clxns.app.BuildConfig

class Constants {
    companion object {
        const val DEMO_USER = "sharukh.s@infiny.in"
        const val TOKEN_TEMP = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InNoYXJ1a2guc0BpbmZpbnkuaW4iLCJwaG9uZSI6Ijc5NzcyNjQ2NjIiLCJ1c2VyX2lkIjoyNiwicm9sZUlkIjo3LCJleHAiOjE4NDcyMDAzOTQsImlhdCI6MTYzMTIwMDM5NH0.B_-JIZ0KGlc4527sioyUz2zZWusoKquNRn_w0Xat38Y"

        const val PREF_NAME = "clxns_pref_${BuildConfig.APPLICATION_ID}"
        const val TOKEN = "token"
        const val USER_NAME = "user_name"
        const val USER_MOBILE = "user_mobile"
        const val USER_EMAIL = "user_email"
        const val USER_ADDRESS = "user_address"
        const val USER_LOCATION = "user_location"
        const val USER_DOB = "user_dob"
        const val USER_BLOOD_GROUP = "user_blood_group"
        const val USER_ID = "user_id"
        const val USER_EMPLOYEE_ID = "user_employee_id"
        const val USER_REPORTING_MANAGER = "user_reporting_manager"
        const val USER_REPORTING_MANAGER_CONTACT = "user_reporting_manager_contact"
        const val IS_USER_LOGGED_IN = "logged_in_status"
        const val SPLASH_SCREEN_TIMEOUT: Long = 1000
    }
}