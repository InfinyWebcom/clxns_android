package com.clxns.app.ui.main.cases.casedetails.casestatus.paymentcollection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.CaseDetailsResponse
import com.clxns.app.data.model.MyPlanModel
import com.clxns.app.data.model.cases.CaseCheckInBody
import com.clxns.app.data.model.home.HomeStatisticsResponse
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

    private val _responseCaseDetails : MutableLiveData<NetworkResult<CaseDetailsResponse>> =
        MutableLiveData()
    val responseCaseDetails : LiveData<NetworkResult<CaseDetailsResponse>> = _responseCaseDetails

    fun getCaseDetails(token : String, loanAccountNumber : String) = viewModelScope.launch {
        repository.getCaseDetails(token, loanAccountNumber).collect { values ->
            _responseCaseDetails.value = values
        }
    }

    private val _responseAddPayment : MutableLiveData<NetworkResult<HomeStatisticsResponse>> =
        MutableLiveData()
    val responseAddPayment : LiveData<NetworkResult<HomeStatisticsResponse>> = _responseAddPayment

    fun addPayment(
        token : String,
        leadId : String,
        loanNo : String,
        amtType : String,
        paymentMode : String,
        recoveryDate : String,
        refNo : String,
        chequeNo : String,
        remark : String,
        supporting : Array<String>
    ) = viewModelScope.launch {
        repository.addPayment(
            token, leadId, loanNo, amtType,
            paymentMode, recoveryDate, refNo, chequeNo, remark, supporting
        ).collect { values ->
            _responseAddPayment.value = values
        }
    }

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