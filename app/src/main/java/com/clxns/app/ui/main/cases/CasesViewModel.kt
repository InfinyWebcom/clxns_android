package com.clxns.app.ui.main.cases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.AddToPlanModel
import com.clxns.app.data.model.cases.CasesResponse
import com.clxns.app.data.repository.CasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CasesViewModel @Inject constructor(
    private val repository: CasesRepository
) : ViewModel() {

    private var _dispositionsResponse: MutableLiveData<List<String>> = MutableLiveData()
    val dispositionsResponse: LiveData<List<String>> = _dispositionsResponse

    private var _subDispositionsResponse: MutableLiveData<List<String>> = MutableLiveData()
    val subDispositionsResponse: LiveData<List<String>> = _subDispositionsResponse

    private var _dispositionsIdResponse: MutableLiveData<Int> = MutableLiveData()
    val dispositionsIdResponse: LiveData<Int> = _dispositionsIdResponse

    private var _subDispositionsIdResponse: MutableLiveData<Int> = MutableLiveData()
    val subDispositionsIdResponse: LiveData<Int> = _subDispositionsIdResponse

    private val _responseCaseList: MutableLiveData<NetworkResult<CasesResponse>> = MutableLiveData()
    val responseCaseList: LiveData<NetworkResult<CasesResponse>> = _responseCaseList

    private val _responseAddToPlan: MutableLiveData<NetworkResult<AddToPlanModel>> =
        MutableLiveData()
    val responseAddToPlan: LiveData<NetworkResult<AddToPlanModel>> = _responseAddToPlan

    //Network Calls
    fun getCasesList(
        token: String,
        searchTxt: String,
        dispositionId: String,
        subDispositionId: String,
        fromDate: String,
        toDate: String
    ) = viewModelScope.launch {
        repository.getCasesList(token, searchTxt, dispositionId, subDispositionId, fromDate, toDate)
            .collect { values ->
                _responseCaseList.value = values
            }
    }

    fun addToPlan(
        token: String,
        leadId: String,
        planDate: String
    ) = viewModelScope.launch {
        repository.addToPlan(token, leadId, planDate).collect { values ->
            _responseAddToPlan.value = values
        }
    }

    //Local calls
    fun getAllDispositionsFromRoomDB() = viewModelScope.launch {
        repository.getAllDispositionsFromRoomDB().collect {
            _dispositionsResponse.value = it
        }
    }

    fun getSubDispositionsFromRoomDB(dispositionId: Int) = viewModelScope.launch {
        repository.getAllSubDispositionsFromRoomDB(dispositionId).collect {
            _subDispositionsResponse.value = it
        }
    }

    fun getDispositionIdFromRoomDB(dispositionName: String) = viewModelScope.launch {
        repository.getDispositionIdFromRoomDB(dispositionName).collect {
            _dispositionsIdResponse.value = it
        }
    }

    fun getSubDispositionIdFromRoomDB(subDispositionName: String) = viewModelScope.launch {
        repository.getSubDispositionIdFromRoomDB(subDispositionName).collect {
            _subDispositionsIdResponse.value = it
        }
    }
}