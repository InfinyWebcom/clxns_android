package com.clxns.app.ui.main.account.changePassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.ChangePasswordResponse
import com.clxns.app.data.repository.ChangePasswordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository : ChangePasswordRepository
) : ViewModel() {

    private val _changePasswordResponse : MutableLiveData<NetworkResult<ChangePasswordResponse>> =
        MutableLiveData()
    val changePasswordResponse : LiveData<NetworkResult<ChangePasswordResponse>> =
        _changePasswordResponse

    fun changePassword(
        token : String,
        newPassword : String,
        confirmPassword : String,
        oldPassword : String
    ) = viewModelScope.launch {
        repository.changePassword(
            token,
            newPassword,
            confirmPassword,
            oldPassword
        ).collect { values ->
            _changePasswordResponse.value = values
        }
    }
}