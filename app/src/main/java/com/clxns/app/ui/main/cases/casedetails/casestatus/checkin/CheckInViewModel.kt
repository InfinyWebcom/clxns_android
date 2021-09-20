package com.clxns.app.ui.main.cases.casedetails.casestatus.checkin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
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


    private val _response: MutableLiveData<NetworkResult<MyPlanModel>> = MutableLiveData()
    val response: LiveData<NetworkResult<MyPlanModel>> = _response

    fun saveCheckInData(
        token: String,
        loanAccountNo: String,
        dispositionId: String,
        subDispositionId: String,
        comments: String,
        file: String,
        followUp: String,
        nextAction: String,
        additionalField: String
    ) = viewModelScope.launch {
        repository.saveCheckInData(
            token, loanAccountNo, dispositionId, subDispositionId,
            comments, file, followUp, nextAction, additionalField
        ).collect { values ->
            _response.value = values
        }
    }


    fun getLatLong(latitude: Double, longitude: Double) {

        Log.i(javaClass.name, "latitude--->" + latitude)
        Log.i(javaClass.name, "longitude--->" + longitude)
    }


}