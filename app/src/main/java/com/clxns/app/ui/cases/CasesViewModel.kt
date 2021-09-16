package com.clxns.app.ui.cases

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.model.CasesResponse
import com.clxns.app.data.repository.CasesRepository
import com.clxns.app.utils.NetworkHelper
import com.clxns.app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CasesViewModel @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val casesRepository: CasesRepository
) : ViewModel() {

    private var _casesResponse : MutableLiveData<Resource<CasesResponse>> = MutableLiveData()
    val casesResponse : LiveData<Resource<CasesResponse>> get() = _casesResponse

    fun getCasesList(token: String) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                casesRepository.getCasesList(token).let {
                    if (it.isSuccessful){
                        _casesResponse.postValue(Resource.success(it.body()))
                    }
                }
            }
        }
    }
}