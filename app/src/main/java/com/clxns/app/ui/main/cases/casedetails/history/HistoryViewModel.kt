package com.clxns.app.ui.main.cases.casedetails.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.CaseHistoryResponse
import com.clxns.app.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<CaseHistoryResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<CaseHistoryResponse>> = _response

    fun getCaseHistory(token: String, loanAccountNumber: String) = viewModelScope.launch {
        repository.getCaseHistory(token, loanAccountNumber).collect { values ->
            _response.value = values
        }
    }
}