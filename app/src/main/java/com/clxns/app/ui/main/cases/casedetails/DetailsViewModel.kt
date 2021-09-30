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
    private val detailsRepository: DetailsRepository
) : ViewModel() {

    private val _responseCaseDetail: MutableLiveData<NetworkResult<CaseDetailsResponse>> =
        MutableLiveData()
    val responseCaseDetail: LiveData<NetworkResult<CaseDetailsResponse>> = _responseCaseDetail

    fun getCaseDetails(token: String, loanAccountNumber: String) = viewModelScope.launch {
        _responseCaseDetail.value = NetworkResult.Loading()
        detailsRepository.getCaseDetails(token, loanAccountNumber).collect { values ->
            _responseCaseDetail.value = values
        }
    }
}