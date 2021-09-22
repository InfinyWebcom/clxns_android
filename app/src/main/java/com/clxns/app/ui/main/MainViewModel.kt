package com.clxns.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.database.DispositionEntity
import com.clxns.app.data.database.SubDispositionEntity
import com.clxns.app.data.model.DispositionResponse
import com.clxns.app.data.model.HomeStatisticsResponse
import com.clxns.app.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {
    private val _responseDisposition: MutableLiveData<NetworkResult<DispositionResponse>> =
        MutableLiveData()
    val responseDisposition: LiveData<NetworkResult<DispositionResponse>> = _responseDisposition

    private val _responseHomeStats: MutableLiveData<NetworkResult<HomeStatisticsResponse>> =
        MutableLiveData()
    val responseHomeStats: LiveData<NetworkResult<HomeStatisticsResponse>> = _responseHomeStats


    private val _response: MutableLiveData<List<DispositionEntity>> = MutableLiveData()
    val response: LiveData<List<DispositionEntity>> = _response


    fun getHomeStatsData(token: String) = viewModelScope.launch {
        mainRepository.getHomeStatsData(token).collect {
            _responseHomeStats.value = it
        }
    }
    fun getAllDispositions() = viewModelScope.launch {
        mainRepository.getAllDispositions().collect {
            _responseDisposition.value = it
        }
    }

    fun saveAllDispositions(dispositionList: List<DispositionEntity>) = viewModelScope.launch {
        mainRepository.saveAllDispositions(dispositionList)
    }

    fun saveAllSubDispositions(subDispositionList: List<SubDispositionEntity>) =
        viewModelScope.launch {
            mainRepository.saveAllSubDispositions(subDispositionList)
        }

    fun getAll() = viewModelScope.launch {
        _response.value = mainRepository.getAll()
    }
}