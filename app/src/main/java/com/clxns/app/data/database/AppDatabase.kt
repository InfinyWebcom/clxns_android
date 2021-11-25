package com.clxns.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Singleton

/** STEP 3 in Implementing ROOM DB
 * This abstract class is used while creating the Room DB with all required parameters such as context, DB_NAME, etc.
 * This class is used by DB Module that does the implementation of the ROOM DB using Hilt - DI
 */
@Singleton
@Database(entities = [DispositionEntity::class, SubDispositionEntity::class, BankDetailsEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun appDao() : AppDao //This function returns instance of AppDao
}