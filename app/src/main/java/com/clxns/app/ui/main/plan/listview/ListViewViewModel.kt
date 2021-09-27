package com.clxns.app.ui.main.plan.listview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.MyPlanModel
import com.clxns.app.data.repository.ListViewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewViewModel @Inject constructor(
    private val repository: ListViewRepository
) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<MyPlanModel>> = MutableLiveData()
    val response: LiveData<NetworkResult<MyPlanModel>> = _response
    fun getMyPlanList(token: String, planDate: String) = viewModelScope.launch {
        repository.getMyPlanList(token,planDate).collect { values ->
            _response.value = values
        }
    }

}