package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.clxns.app.R
import com.clxns.app.databinding.CasesSummaryLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CaseSummaryBottomSheet : BottomSheetDialogFragment() {

    lateinit var casesSummaryLayoutBinding: CasesSummaryLayoutBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        casesSummaryLayoutBinding = CasesSummaryLayoutBinding.inflate(layoutInflater)
        return casesSummaryLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        casesSummaryLayoutBinding.caseSummaryCloseBtn.setOnClickListener {
            dismiss()
        }

        casesSummaryLayoutBinding.caseSummaryPtpBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummaryRtpBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummaryBrokenPtpBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummaryDisputeBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummaryCustomerNotFoundBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummaryCallbackBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummaryCollectBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummaryPartiallyCollectBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummarySettlementBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        casesSummaryLayoutBinding.caseSummaryCustomerDeceasedBtn.setOnClickListener {
            navigateToCasesScreen(10)
        }

    }

    private fun navigateToCasesScreen(dispositionId: Int) {
        val actions = CaseSummaryBottomSheetDirections.actionNavigationHomeSummaryToNavigationCases(
            dispositionId
        )
        findNavController().navigate(actions)
        dismiss()
    }


}