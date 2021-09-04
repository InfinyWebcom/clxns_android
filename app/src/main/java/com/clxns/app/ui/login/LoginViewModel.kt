package com.clxns.app.ui.login

import android.util.Patterns
import androidx.lifecycle.*
import com.clxns.app.R
import com.clxns.app.data.model.LoginResponse
import com.clxns.app.data.repository.LoginRepository
import com.clxns.app.utils.NetworkHelper
import com.clxns.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResponse : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse : LiveData<Resource<LoginResponse>> get() = _loginResponse

    fun performLogin(mobileNo:String, password:String){
        viewModelScope.launch {
            _loginResponse.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()){
                loginRepository.performLogin(mobileNo, password).let {
                    if (it!!.isSuccessful){
                        _loginResponse.postValue(Resource.success(it.body()))
                    }else _loginResponse.postValue(Resource.error(it.errorBody().toString(), null))
                }
            }else _loginResponse.postValue(Resource.error("No internet connection", null))
        }
    }

    fun loginDataChanged(mobileNo: String, password: String) {
        if (!isMobileNumberValid(mobileNo)) {
            _loginForm.value = LoginFormState(mobileNumberError = R.string.invalid_mobile, isDataValid = false)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password, isDataValid = false)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isMobileNumberValid(mobileNo: String): Boolean {
        return mobileNo.length >= 10
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}