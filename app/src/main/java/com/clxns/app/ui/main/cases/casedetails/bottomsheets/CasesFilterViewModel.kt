package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.database.LocalDataSource
import com.clxns.app.data.repository.CasesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CasesFilterViewModel @Inject constructor(private val casesRepository: CasesRepository) : ViewModel() {
    private var _dispositionsResponse : MutableLiveData<List<String>> = MutableLiveData()
    val dispositionsResponse : LiveData<List<String>> = _dispositionsResponse

    fun getAllDispositions() = viewModelScope.launch {
        //casesRepository.getAllDisposition().collect {
          //  _dispositionsResponse.value = it
        //}
    }
}