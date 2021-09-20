package com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions.map

import androidx.lifecycle.ViewModel
import com.clxns.app.data.repository.RepossesionMapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RepossesionMapViewModel@Inject constructor(
    private val repository: RepossesionMapRepository
) : ViewModel() {

}