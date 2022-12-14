package com.clxns.app.ui.main.plan.plannedLeads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.model.MyPlanModel
import com.clxns.app.data.repository.MyPlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MyPlanViewModel @Inject constructor(
    private val repository : MyPlanRepository
) : ViewModel() {

    lateinit var calendar : Calendar
    val monthArray =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var mYear : Int = 0
    var mMonth : Int = 0
    var mDay : Int = 0
    lateinit var currentDate : String

    private val _response : MutableLiveData<NetworkResult<MyPlanModel>> = MutableLiveData()
    val response : LiveData<NetworkResult<MyPlanModel>> = _response


    fun getMyPlanList(token : String, planDate : String) = viewModelScope.launch {
        _response.value = NetworkResult.Loading()
        repository.getMyPlanList(token, planDate).collect { values ->
            _response.value = values
        }
    }

    fun getCurrentDate() {
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
        currentDate = "$mYear-${String.format("%02d", mMonth + 1)}-${String.format("%02d", mDay)}"
    }

}