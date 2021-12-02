package com.clxns.app.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.home.HomeStatisticsResponse
import com.clxns.app.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository : HomeRepository) : ViewModel() {


    private val _responseHomeStats : MutableLiveData<NetworkResult<HomeStatisticsResponse>> =
        MutableLiveData()
    val responseHomeStats : LiveData<NetworkResult<HomeStatisticsResponse>> = _responseHomeStats

    fun getHomeStatsData(token : String) = viewModelScope.launch {
        _responseHomeStats.value = NetworkResult.Loading()
        homeRepository.getHomeStatsData(token).collect {
            _responseHomeStats.value = it
        }
    }
}