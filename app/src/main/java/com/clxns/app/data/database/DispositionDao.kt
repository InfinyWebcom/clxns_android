package com.clxns.app.data.database

import androidx.room.*

@Dao
interface DispositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDispositions(dispositionEntity: DispositionEntity)

    @Transaction
    suspend fun createAllDispositions(dispositionList: List<DispositionEntity>) =
        dispositionList.forEach { insertAllDispositions(it) }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSubDispositions(subDispositionEntity: SubDispositionEntity)

    @Transaction
    suspend fun createAllSubDispositions(subDispositionList: List<SubDispositionEntity>) =
        subDispositionList.forEach { insertAllSubDispositions(it) }

    @Query("Select * from disposition_table")
    suspend fun getAll(): List<DispositionEntity>


    @Query("Select name from disposition_table order by id ASC")
    fun getAllDispositionsFromRoomDB(): List<String>

    @Query("Select * from sub_disposition_table where dispositionId = :dispositionId")
    fun getDispositionId(dispositionId: String): List<SubDispositionEntity>


}