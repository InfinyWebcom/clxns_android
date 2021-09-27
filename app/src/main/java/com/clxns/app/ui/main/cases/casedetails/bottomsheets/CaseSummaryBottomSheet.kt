package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clxns.app.R
import com.clxns.app.data.model.home.SummaryData
import com.clxns.app.databinding.CasesSummaryLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CaseSummaryBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding: CasesSummaryLayoutBinding

    private lateinit var summaryData: SummaryData

    private val caseArgs by navArgs<CaseSummaryBottomSheetArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        summaryData = caseArgs.summaryData!!
        binding = CasesSummaryLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        setListeners()

    }

    private fun setListeners() {
        binding.caseSummaryCloseBtn.setOnClickListener {
            dismiss()
        }

        binding.caseSummaryPtpBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummaryRtpBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummaryBrokenPtpBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummaryDisputeBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummaryCustomerNotFoundBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummaryCallbackBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummaryCollectBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummaryPartiallyCollectBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummarySettlementBtn.setOnClickListener {
            navigateToCasesScreen(5)
        }
        binding.caseSummaryCustomerDeceasedBtn.setOnClickListener {
            navigateToCasesScreen(10)
        }
    }

    private fun updateUI() {
        binding.totalCasesTv.text = summaryData.totalCases.toString()
        binding.ptpTv.text = summaryData.promiseToPay.toString()
        binding.rtpTv.text = summaryData.denialRTP.toString()
        binding.brokenPtpTv.text = summaryData.brokenPTP.toString()
        binding.disputeTv.text = summaryData.dispute.toString()
        binding.customerNotFoundTv.text = summaryData.customerNotFound.toString()
        binding.callBackTv.text = summaryData.callBack.toString()
        binding.collectTv.text = summaryData.collect.toString()
        binding.partiallyCollectTv.text = summaryData.partiallyCollect.toString()
        binding.settlementTv.text = summaryData.settlementForeclosure.toString()
        binding.customerDeceasedTv.text = summaryData.customerDeceased.toString()
    }

    private fun navigateToCasesScreen(dispositionId: Int) {
        val actions = CaseSummaryBottomSheetDirections.actionNavigationHomeSummaryToNavigationCases(
            dispositionId
        )
        findNavController().navigate(actions)
        dismiss()
    }


}