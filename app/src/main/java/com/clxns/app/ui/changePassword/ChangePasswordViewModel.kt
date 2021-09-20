package com.clxns.app.ui.changePassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.ChangePasswordResponse
import com.clxns.app.data.repository.ChangePasswordRepository
import com.clxns.app.utils.NetworkHelper
import com.clxns.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: ChangePasswordRepository
) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<ChangePasswordResponse>> =
        MutableLiveData()
    val response: LiveData<NetworkResult<ChangePasswordResponse>> = _response
    fun changePassword(
        token: String,
        newPassword: String,
        confirmPassword: String,
        oldPassword: String
    ) = viewModelScope.launch {
        repository.changePassword(
            token,
            newPassword,
            confirmPassword,
            oldPassword
        ).collect { values ->
            _response.value = values
        }
    }

//    private val _changePasswordResponse: MutableLiveData<Resource<ChangePasswordResponse>> =
//        MutableLiveData()
//    val changePasswordResponse: LiveData<Resource<ChangePasswordResponse>> get() = _changePasswordResponse
//
//    fun changePassword(
//        token: String,
//        newPassword: String,
//        confirmPassword: String,
//        oldPassword: String
//    ) {
//        viewModelScope.launch {
//            if (networkHelper.isNetworkConnected()) {
//                _changePasswordResponse.postValue(Resource.loading(null))
//                changePasswordRepository.changePassword(
//                    token,
//                    newPassword,
//                    confirmPassword,
//                    oldPassword
//                ).let {
//                    if (it.isSuccessful) {
//                        if (it.body()?.error == true) {
//                            _changePasswordResponse.postValue(
//                                Resource.error(
//                                    it.body()!!.title,
//                                    null
//                                )
//                            )
//                        } else {
//                            _changePasswordResponse.postValue(Resource.success(it.body()))
//                        }
//                    } else {
//                        _changePasswordResponse.postValue(
//                            Resource.error(
//                                "Something went wrong",
//                                null
//                            )
//                        )
//                    }
//                }
//            } else {
//                _changePasswordResponse.postValue(Resource.error("No internet connection", null))
//            }
//        }
//    }

}