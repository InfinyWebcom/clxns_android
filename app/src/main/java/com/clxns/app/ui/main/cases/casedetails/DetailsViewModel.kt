package com.clxns.app.ui.main.cases.casedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.repository.DetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: DetailsRepository
) : ViewModel() {
    var loanAccountNumber: String? = null

    private val _response: MutableLiveData<NetworkResult<CaseDetailsResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<CaseDetailsResponse>> = _response

    fun getCaseDetails(token: String, loanAccountNumber: String) = viewModelScope.launch {
        repository.getCaseDetails(token, loanAccountNumber).collect { values ->
            _response.value = values
        }
    }
}