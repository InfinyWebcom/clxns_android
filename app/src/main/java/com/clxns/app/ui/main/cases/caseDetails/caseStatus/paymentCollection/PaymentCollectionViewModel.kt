package com.clxns.app.ui.main.cases.caseDetails.caseStatus.paymentCollection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.MyPlanModel
import com.clxns.app.data.model.cases.CaseCheckInBody
import com.clxns.app.data.repository.PaymentCollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentCollectionViewModel @Inject constructor(
    private val repository : PaymentCollectionRepository
) : ViewModel() {

    var loanAccountNumber : String? = null
    var dispositionId : String? = null
    var location : String? = null
    lateinit var mainSupporting : ArrayList<String>

    private val _responseSaveCheckIn : MutableLiveData<NetworkResult<MyPlanModel>> =
        MutableLiveData()
    val responseSaveCheckIn : LiveData<NetworkResult<MyPlanModel>> = _responseSaveCheckIn

    fun saveCheckInData(
        token : String,
        body : CaseCheckInBody
    ) = viewModelScope.launch {
        _responseSaveCheckIn.value = NetworkResult.Loading()
        repository.saveCheckInData(
            token, body
        ).collect { values ->
            _responseSaveCheckIn.value = values
        }
    }
}