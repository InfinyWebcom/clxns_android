package com.clxns.app.ui.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.LoginResponse
import com.clxns.app.data.model.LogoutResponse
import com.clxns.app.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    private val _responseLogout: MutableLiveData<NetworkResult<LogoutResponse>> = MutableLiveData()
    val responseLogout: LiveData<NetworkResult<LogoutResponse>> = _responseLogout

    private val _responseUserDetails: MutableLiveData<NetworkResult<LoginResponse>> =
        MutableLiveData()
    val responseUserDetails: LiveData<NetworkResult<LoginResponse>> = _responseUserDetails

    private val _responseBankNames : MutableLiveData<List<String>> = MutableLiveData()
    val responseBankNames : LiveData<List<String>> = _responseBankNames

    private val _responseBankImage : MutableLiveData<String> = MutableLiveData()
    val responseBankImage : LiveData<String> = _responseBankImage

    fun getBankNameList() = viewModelScope.launch {
        repository.getBankNameList().collect {
            _responseBankNames.value = it
        }
    }

    fun getBankImage(bankName:String) = viewModelScope.launch {
        repository.getBankImage(bankName).collect {
            _responseBankImage.value = it
        }
    }

    fun logout(token: String) = viewModelScope.launch {
        _responseLogout.value = NetworkResult.Loading()
        repository.logout(token).collect { values ->
            _responseLogout.value = values
        }
    }

    fun getUserDetails(token: String) = viewModelScope.launch {
        _responseUserDetails.value = NetworkResult.Loading()
        repository.getUserDetails(token).collect {
            _responseUserDetails.value = it
        }
    }

}