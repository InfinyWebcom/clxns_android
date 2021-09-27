package com.clxns.app.data.database

import androidx.room.*

@Dao
interface AppDao {
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


    @Query("Select name from disposition_table order by id ASC")
    fun getAllDispositions(): List<String>

    @Query("Select name from sub_disposition_table where dispositionId= :id")
    fun getAllSubDispositions(id: Int): List<String>

    @Query("Select id from disposition_table where name=:dispositionName")
    fun getDispositionId(dispositionName: String) : Int


    @Query("Select id from sub_disposition_table where name=:subDispositionName")
    fun getSubDispositionId(subDispositionName: String) : Int


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBankDetails(bankDetailsEntity: BankDetailsEntity)

    @Transaction
    suspend fun createBankListInDB(bankDetailList: List<BankDetailsEntity>) =
        bankDetailList.forEach { insertAllBankDetails(it) }

    @Query("Select name from bank_details")
    fun getBankNameList(): List<String>

    @Query("Select fi_image from bank_details where name = :bankName")
    fun getBankImageFromDB(bankName: String): String


}