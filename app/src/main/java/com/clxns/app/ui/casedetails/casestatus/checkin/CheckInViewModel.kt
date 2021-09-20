package com.clxns.app.ui.casedetails.casestatus.checkin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.clxns.app.data.repository.CheckInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel@Inject constructor(
    private val repository: CheckInRepository
) : ViewModel() {

    fun getLatLong(latitude: Double, longitude: Double) {

        Log.i(javaClass.name,"latitude--->"+latitude)
        Log.i(javaClass.name,"longitude--->"+longitude)
    }


}