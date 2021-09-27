package com.clxns.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Singleton

@Singleton
@Database(entities = [DispositionEntity::class, SubDispositionEntity::class, BankDetailsEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun dispositionDao() : AppDao
}