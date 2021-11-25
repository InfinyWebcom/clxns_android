package com.clxns.app.data.preference

import android.content.SharedPreferences
import androidx.annotation.NonNull
import javax.inject.Inject

class SessionManager @Inject constructor(private val sharedPreferences: SharedPreferences){
    fun saveAnyData(@NonNull key:String, value : Any){
        val editor = sharedPreferences.edit()
        when(value){
            is Int -> editor.putInt(key, value)
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
            else -> {}
        }
        editor.apply()
    }

    fun getInt(@NonNull key: String) = sharedPreferences.getInt(key, 0)
    fun getString(@NonNull key: String) = sharedPreferences.getString(key, "")
    fun getBoolean(@NonNull key: String) = sharedPreferences.getBoolean(key, false)
    fun getLong(@NonNull key: String) = sharedPreferences.getLong(key, 0L)

    fun removeData(@NonNull key: String){
        val editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun clearAll(){
        sharedPreferences.edit().clear().apply()
    }

}