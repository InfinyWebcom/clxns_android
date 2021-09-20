package com.clxns.app.ui.main.cases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.AddToPlanModel
import com.clxns.app.data.model.CasesResponse
import com.clxns.app.data.repository.CasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CasesViewModel @Inject constructor(
    private val repository: CasesRepository
) : ViewModel() {

    private val _responseCaseList: MutableLiveData<NetworkResult<CasesResponse>> = MutableLiveData()
    val responseCaseList: LiveData<NetworkResult<CasesResponse>> = _responseCaseList

    fun getCasesList(token: String, searchTxt:String) = viewModelScope.launch {
        repository.getCasesList(token,searchTxt).collect { values ->
            _responseCaseList.value = values
        }
    }

    private val _responseAddToPlan: MutableLiveData<NetworkResult<AddToPlanModel>> =
        MutableLiveData()
    val responseAddToPlan: LiveData<NetworkResult<AddToPlanModel>> = _responseAddToPlan

    fun addToPlan(
        token: String,
        leadId: String,
        planDate: String
    ) = viewModelScope.launch {
        repository.addToPlan(token, leadId, planDate).collect { values ->
            _responseAddToPlan.value = values
        }
    }
}