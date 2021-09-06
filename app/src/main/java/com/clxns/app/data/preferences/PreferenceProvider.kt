package com.clxns.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext

private const val KEY_SAVED_AT = "key_saved_at"

class PreferenceProvider(
    @ApplicationContext private val context: Context
) {
    private val preference: SharedPreferences
        get() = context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )

    fun saveLastSavedAt(savedAt: String) {
        preference.edit().putString(
            KEY_SAVED_AT, savedAt
        ).apply()
    }

    fun getLastSavedAt(): String? {
        return preference.getString(KEY_SAVED_AT, null)
    }

}