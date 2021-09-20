package com.clxns.app.ui.home.account

import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.LoginResponse
import com.clxns.app.data.model.LogoutResponse
import com.clxns.app.data.repository.ProfileRepository
import com.clxns.app.utils.NetworkHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _responseLogout: MutableLiveData<NetworkResult<LogoutResponse>> = MutableLiveData()
    val responseLogout: LiveData<NetworkResult<LogoutResponse>> = _responseLogout

    fun logout(token: String) = viewModelScope.launch {
        repository.logout(token).collect { values ->
            _responseLogout.value = values
        }
    }

}