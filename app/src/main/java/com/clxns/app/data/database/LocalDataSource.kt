package com.clxns.app.data.database

import javax.inject.Inject

// This Local Data Source class is used for performing coroutines operation to fetch the data from the ROOM DB
class LocalDataSource @Inject constructor(private val appDao: AppDao) {

    suspend fun saveAllDispositions(dispositionList: List<DispositionEntity>) =
        appDao.createAllDispositions(dispositionList)

    suspend fun saveAllSubDispositions(subDispositionList: List<SubDispositionEntity>) =
        appDao.createAllSubDispositions(subDispositionList)

    suspend fun saveAllBankDetails(bankDetailList: List<BankDetailsEntity>) =
        appDao.createBankListInDB(bankDetailList)

    fun getBankNameList() = appDao.getBankNameList()

    fun getBankImage(bankName: String) = appDao.getBankImageFromDB(bankName)

    fun getAllDispositionsFromRoomDB() = appDao.getAllDispositions()

    fun getDispositionId(dispositionName:String) = appDao.getDispositionId(dispositionName)

    fun getDispositionName(dispositionID:Int) = appDao.getDispositionName(dispositionID)

    fun getSubDispositionId(subDispositionName: String) = appDao.getSubDispositionId(subDispositionName)

    fun getSubDispositionName(subDispositionID:Int) = appDao.getSubDispositionName(subDispositionID)

    fun getAllSubDispositionsFromRoomDB(dispositionId:Int) = appDao.getAllSubDispositions(dispositionId)
}