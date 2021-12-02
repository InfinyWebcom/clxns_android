package com.clxns.app.ui.login.forgotPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.ForgotPasswordResponse
import com.clxns.app.data.repository.ForgotPasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository : ForgotPasswordRepository
) : ViewModel() {

    private val _responseGetOTP : MutableLiveData<NetworkResult<ForgotPasswordResponse>> =
        MutableLiveData()
    val responseGetOTP : LiveData<NetworkResult<ForgotPasswordResponse>> = _responseGetOTP
    fun getOTP(emailId : String) = viewModelScope.launch {
        _responseGetOTP.value = NetworkResult.Loading()
        repository.getOTP(emailId).collect { values ->
            _responseGetOTP.value = values
        }
    }

    private val _responseVerifyOTP : MutableLiveData<NetworkResult<ForgotPasswordResponse>> =
        MutableLiveData()
    val responseVerifyOTP : LiveData<NetworkResult<ForgotPasswordResponse>> = _responseVerifyOTP
    fun verifyOTP(token : String, otp : String, emailId : String) = viewModelScope.launch {
        _responseVerifyOTP.value = NetworkResult.Loading()
        repository.verifyOTP(token, otp, emailId).collect { values ->
            _responseVerifyOTP.value = values
        }
    }
}