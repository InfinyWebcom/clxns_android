package com.clxns.app.data.database

import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dispositionDao: DispositionDao) {

    suspend fun saveAllDispositions(dispositionList: List<DispositionEntity>) =
        dispositionDao.createAllDispositions(dispositionList)

    suspend fun saveAllSubDispositions(subDispositionList: List<SubDispositionEntity>) =
        dispositionDao.createAllSubDispositions(subDispositionList)


    suspend fun getAll() = dispositionDao.getAll()

    suspend fun getAllDispositions() = dispositionDao.getAllDispositionsFromRoomDB()
    /**
     *Function to get all the dispositions from the room db
    suspend fun getAllDisposition() : Flow<List<String>>{
    return flow {
    emit(localDataSource.getAllDispositions())
    }.flowOn(Dispatchers.IO)
    }
     */
}