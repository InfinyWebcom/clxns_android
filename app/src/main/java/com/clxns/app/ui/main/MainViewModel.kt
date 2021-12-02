package com.clxns.app.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.database.BankDetailsEntity
import com.clxns.app.data.database.DispositionEntity
import com.clxns.app.data.database.SubDispositionEntity
import com.clxns.app.data.model.DispositionResponse
import com.clxns.app.data.model.FISBankResponse
import com.clxns.app.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository : MainRepository) : ViewModel() {
    private val _responseDisposition : MutableLiveData<NetworkResult<DispositionResponse>> =
        MutableLiveData()
    val responseDisposition : LiveData<NetworkResult<DispositionResponse>> = _responseDisposition

    private val _responseBankList : MutableLiveData<NetworkResult<FISBankResponse>> =
        MutableLiveData()
    val responseBankList : LiveData<NetworkResult<FISBankResponse>> = _responseBankList


    fun getAllDispositions() = viewModelScope.launch {
        mainRepository.getAllDispositions().collect {
            _responseDisposition.value = it
        }
    }

    fun getBankList(token : String) = viewModelScope.launch {
        mainRepository.getBankList(token).collect {
            _responseBankList.value = it
        }
    }

    //Saving Disposition in the local db
    fun saveAllDispositions(dispositionList : List<DispositionEntity>) = viewModelScope.launch {
        mainRepository.saveAllDispositions(dispositionList)
    }

    //Saving Sub Disposition in the local db
    fun saveAllSubDispositions(subDispositionList : List<SubDispositionEntity>) =
        viewModelScope.launch {
            mainRepository.saveAllSubDispositions(subDispositionList)
        }

    //Saving Bank List in the local db
    fun saveAllBankDetails(bankDetailList : List<BankDetailsEntity>) =
        viewModelScope.launch {
            mainRepository.saveAllBankDetails(bankDetailList)
        }
}