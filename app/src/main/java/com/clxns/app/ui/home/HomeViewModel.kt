package com.clxns.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.DispositionResponse
import com.clxns.app.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {


    private val _responseDisposition: MutableLiveData<NetworkResult<DispositionResponse>> =
        MutableLiveData()
    val responseDisposition: LiveData<NetworkResult<DispositionResponse>> = _responseDisposition

    fun getAllDispositions() = viewModelScope.launch {
        homeRepository.getAllDispositions().collect {
            _responseDisposition.value = it
        }
    }
}