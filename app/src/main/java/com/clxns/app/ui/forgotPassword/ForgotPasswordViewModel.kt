package com.clxns.app.ui.forgotPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.ForgotPasswordResponse
import com.clxns.app.data.repository.ForgotPasswordRepository
import com.clxns.app.utils.NetworkHelper
import com.clxns.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: ForgotPasswordRepository
) : ViewModel() {

    private val _responseGetOTP: MutableLiveData<NetworkResult<ForgotPasswordResponse>> =
        MutableLiveData()
    val responseGetOTP: LiveData<NetworkResult<ForgotPasswordResponse>> = _responseGetOTP
    fun getOTP(emailId: String) = viewModelScope.launch {
        repository.getOTP(emailId).collect { values ->
            _responseGetOTP.value = values
        }
    }

    private val _responseVerifyOTP: MutableLiveData<NetworkResult<ForgotPasswordResponse>> =
        MutableLiveData()
    val responseVerifyOTP: LiveData<NetworkResult<ForgotPasswordResponse>> = _responseVerifyOTP
    fun verifyOTP(token: String, otp: String, emailId: String) = viewModelScope.launch {
        repository.verifyOTP(token, otp, emailId).collect { values ->
            _responseVerifyOTP.value = values
        }
    }

//    private val _forgotPasswordResponse: MutableLiveData<Resource<ForgotPasswordResponse>> =
//        MutableLiveData()
//    val forgotPasswordResponse: LiveData<Resource<ForgotPasswordResponse>> get() = _forgotPasswordResponse
//
//    private val _verifyOTPResponse: MutableLiveData<Resource<ForgotPasswordResponse>> =
//        MutableLiveData()
//    val verifyOTPResponse: LiveData<Resource<ForgotPasswordResponse>> get() = _verifyOTPResponse
//
//    fun getOTPFromDB(emailId: String) {
//        viewModelScope.launch {
//            if (networkHelper.isNetworkConnected()) {
//                _forgotPasswordResponse.postValue(Resource.loading(null))
//                forgotPasswordRepository.getOTP(emailId).let {
//                    if (it.isSuccessful) {
//                        if (it.body()?.error == true) {
//                            _forgotPasswordResponse.postValue(
//                                Resource.error(
//                                    "Incorrect email address",
//                                    null
//                                )
//                            )
//                        } else {
//                            _forgotPasswordResponse.postValue(Resource.success(it.body()))
//                        }
//                    } else {
//                        _forgotPasswordResponse.postValue(Resource.error("Something went wrong", null))
//                    }
//                }
//            } else {
//                _forgotPasswordResponse.postValue(Resource.error("No internet connection", null))
//            }
//        }
//    }
//
//    fun verifyOTP(token: String, otp: String, emailId: String) {
//        viewModelScope.launch {
//            if (networkHelper.isNetworkConnected()) {
//                _verifyOTPResponse.postValue(Resource.loading(null))
//                forgotPasswordRepository.verifyOTP(token, otp, emailId).let {
//                    if (it.isSuccessful) {
//                        if (it.body()?.error == true) {
//                            _verifyOTPResponse.postValue(
//                                Resource.error(
//                                    it.body()!!.title,
//                                    null
//                                )
//                            )
//                        } else {
//                            _verifyOTPResponse.postValue(Resource.success(it.body()))
//                        }
//                    } else {
//                        _verifyOTPResponse.postValue(Resource.error("Something went wrong", null))
//                    }
//                }
//            } else {
//                _verifyOTPResponse.postValue(Resource.error("No internet connection", null))
//            }
//        }
//    }
}