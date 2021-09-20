package com.clxns.app.ui.cases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.CasesResponse
import com.clxns.app.data.repository.CasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CasesViewModel @Inject constructor(
    private val repository: CasesRepository
) : ViewModel() {

    private val _response: MutableLiveData<NetworkResult<CasesResponse>> = MutableLiveData()
    val response: LiveData<NetworkResult<CasesResponse>> = _response
    fun getCasesList(token: String, searchTxt:String) = viewModelScope.launch {
        repository.getCasesList(token, searchTxt).collect { values ->
            _response.value = values
        }
    }

//    private var _casesResponse : MutableLiveData<Resource<CasesResponse>> = MutableLiveData()
//    val casesResponse : LiveData<Resource<CasesResponse>> get() = _casesResponse

//    fun getCasesList(token: String) {
//        viewModelScope.launch {
//            if (networkHelper.isNetworkConnected()){
//                casesRepository.getCasesList(token).let {
//                    if (it.isSuccessful){
//                        if (it.body()?.data.isNullOrEmpty()){
//                            _casesResponse.postValue(Resource.error("Empty list",null))
//                        }else {
//                            _casesResponse.postValue(Resource.success(it.body()))
//                        }
//                    }
//                }
//            }
//        }
//    }
}