package com.clxns.app.ui.home.plan.listview

import androidx.lifecycle.*
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.MyPlanResponse
import com.clxns.app.data.repository.ListViewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewViewModel @Inject constructor(
    private val repository: ListViewRepository
) :ViewModel() {

//    private var _myPlanResponse: MutableLiveData<Resource<BlogModel>> = MutableLiveData()
//    val myPlanResponse: LiveData<Resource<BlogModel>> get() = _myPlanResponse

//    suspend fun getMyPlanList(token: String) = withContext(Dispatchers.IO) {
//        viewModelScope.launch {
//            if (networkHelper.isNetworkConnected()) {
//                repository.getMyPlanList(token).let {
//                    if (it.isSuccessful) {
//                        _myPlanResponse.postValue(Resource.success(it.body()))
//                    }
//                }
//            }
//        }
//    }


    private val _response: MutableLiveData<NetworkResult<MyPlanResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<MyPlanResponse>> = _response
    fun getMyPlanList(token: String) = viewModelScope.launch {
        repository.getMyPlanList(token).collect { values ->
            _response.value = values
        }
    }

//    suspend fun userLogin(
//        email: String,
//        password: String
//    ) = withContext(Dispatchers.IO) { repository.userLogin(email, password) }


}