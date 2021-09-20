package com.clxns.app.ui.casedetails

import androidx.lifecycle.ViewModel
import com.clxns.app.data.repository.DetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel@Inject constructor(
    private val repository: DetailsRepository
) : ViewModel() {

}