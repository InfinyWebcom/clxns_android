package com.clxns.app.di

import android.content.Context
import androidx.room.Room
import com.clxns.app.data.database.AppDatabase
import com.clxns.app.data.database.AppDao
import com.clxns.app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    fun provideRoomDB(@ApplicationContext context: Context) : AppDatabase{
        return Room.databaseBuilder(context, AppDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDispositionDao(appDatabase: AppDatabase): AppDao{
        return appDatabase.dispositionDao()
    }
}