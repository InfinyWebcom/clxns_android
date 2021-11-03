package com.clxns.app.ui.main.cases.caseDetails

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

    private val _responseDispositionName: MutableLiveData<String> = MutableLiveData()
    val responseDispositionName: LiveData<String> = _responseDispositionName

    private val _responseSubDispositionName: MutableLiveData<String> = MutableLiveData()
    val responseSubDispositionName: LiveData<String> = _responseSubDispositionName

    fun getCaseDetails(token: String, loanAccountNumber: String) = viewModelScope.launch {
        _responseCaseDetail.value = NetworkResult.Loading()
        detailsRepository.getCaseDetails(token, loanAccountNumber).collect { values ->
            _responseCaseDetail.value = values
        }
    }

    fun getDispositionName(dispositionId: Int) = viewModelScope.launch {
        detailsRepository.getDispositionName(dispositionId).collect {
            _responseDispositionName.value = it
        }
    }

    fun getSubDispositionName(subDispositionId: Int) = viewModelScope.launch {
        detailsRepository.getSubDispositionName(subDispositionId).collect {
            _responseSubDispositionName.value = it
        }
    }
}