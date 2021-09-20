package com.clxns.app.ui.main.cases.casedetails.casestatus.paymentcollection

import androidx.lifecycle.ViewModel
import com.clxns.app.data.repository.PaymentCollectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentCollectionViewModel @Inject constructor(
    private val repository: PaymentCollectionRepository
) : ViewModel() {

}