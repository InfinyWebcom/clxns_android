package com.clxns.app.di

import android.content.Context
import android.content.SharedPreferences
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideSessionManager(preferences: SharedPreferences) =
        SessionManager(preferences)
}