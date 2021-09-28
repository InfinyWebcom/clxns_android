package com.clxns.app.ui.main.cases.casedetails.casestatus.checkin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.LeadContactUpdateResponse
import com.clxns.app.data.model.MyPlanModel
import com.clxns.app.data.repository.CheckInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val repository: CheckInRepository
) : ViewModel() {
    var leadId: String? = null
    var lat: String? = ""
    var long: String? = ""

    private val _responseSaveCheckIn: MutableLiveData<NetworkResult<MyPlanModel>> =
        MutableLiveData()
    val responseSaveCheckIn: LiveData<NetworkResult<MyPlanModel>> = _responseSaveCheckIn

    fun saveCheckInData(
        token: String,
        loanAccountNo: String,
        dispositionId: String,
        subDispositionId: String?,
        comments: String,
        followUp: String,
        nextAction: String,
        additionalField: String,
        location: String,
        supporting: List<String>
    ) = viewModelScope.launch {
        repository.saveCheckInData(
            token, loanAccountNo, dispositionId, subDispositionId,
            comments, followUp, nextAction, additionalField, location, supporting
        ).collect { values ->
            _responseSaveCheckIn.value = values
        }
    }

    private val _responseLeadContactUpdate: MutableLiveData<NetworkResult<LeadContactUpdateResponse>> =
        MutableLiveData()
    val responseLeadContactUpdate: LiveData<NetworkResult<LeadContactUpdateResponse>> =
        _responseLeadContactUpdate

    fun leadContactUpdate(
        token: String,
        leadId: String,
        type: String,
        content: String
    ) = viewModelScope.launch {
        repository.leadContactUpdate(
            token, leadId, type, content
        ).collect { values ->
            _responseLeadContactUpdate.value = values
        }
    }

    private val _responseCaseDetails: MutableLiveData<NetworkResult<CaseDetailsResponse>> =
        MutableLiveData()
    val responseCaseDetails: LiveData<NetworkResult<CaseDetailsResponse>> = _responseCaseDetails

    fun getCaseDetails(token: String, loanAccountNumber: String) = viewModelScope.launch {
        repository.getCaseDetails(token, loanAccountNumber).collect { values ->
            _responseCaseDetails.value = values
        }
    }


    private var _dispositionsIdResponse: MutableLiveData<Int> = MutableLiveData()
    val dispositionsIdResponse: LiveData<Int> = _dispositionsIdResponse

    private var _subDispositionsIdResponse: MutableLiveData<Int> = MutableLiveData()
    val subDispositionsIdResponse: LiveData<Int> = _subDispositionsIdResponse

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

    fun setLocation(latitude: Double, longitude: Double) {
        lat = latitude.toString()
        long=longitude.toString()
        Log.i(javaClass.name, "latitude--->" + latitude)
        Log.i(javaClass.name, "longitude--->" + longitude)
    }


}