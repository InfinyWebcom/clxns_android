package com.clxns.app.ui.main.cases.casedetails.casestatus.repossesions

import androidx.lifecycle.ViewModel
import com.clxns.app.data.repository.RepossesionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RepossessionViewModel @Inject constructor(
    private val repository: RepossesionRepository
) : ViewModel() {
}