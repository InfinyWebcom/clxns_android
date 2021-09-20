package com.clxns.app.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.LoginResponse
import com.clxns.app.data.repository.LoginRepository
import com.clxns.app.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _response: MutableLiveData<NetworkResult<LoginResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<LoginResponse>> = _response
    fun performLogin(emailId: String, password: String) = viewModelScope.launch {
        _response.value = NetworkResult.Loading()
        repository.performLogin(emailId, password).collect { values ->
            _response.value = values
        }
    }

    fun loginDataChanged(emailId: String) {
        if (!isEmailAddressValid(emailId)) {
            _loginForm.value =
                LoginFormState(emailAddressError = R.string.invalid_email, isDataValid = false)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isEmailAddressValid(emailId: String): Boolean {
        return emailId.isValidEmail()
    }
}