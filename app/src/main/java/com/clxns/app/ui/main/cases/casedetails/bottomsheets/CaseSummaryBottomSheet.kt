package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clxns.app.R
import com.clxns.app.data.model.home.SummaryData
import com.clxns.app.databinding.CasesSummaryLayoutBinding
import com.clxns.app.ui.main.cases.CasesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaseSummaryBottomSheet : BottomSheetDialogFragment() {

    lateinit var binding : CasesSummaryLayoutBinding

    private lateinit var summaryData : SummaryData

    private val casesViewModel : CasesViewModel by viewModels()

    private lateinit var fromDate : String
    private lateinit var toDate : String


    private val caseArgs by navArgs<CaseSummaryBottomSheetArgs>()
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }


    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View {
        summaryData = caseArgs.summaryData!!
        binding = CasesSummaryLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()

        setListeners()

        subscribeObserver()
    }

    private fun subscribeObserver() {

        casesViewModel.dispositionsIdResponse.observe(viewLifecycleOwner) {
            if (it != 0) {
                navigateToCasesScreen(it)
            }
        }
    }

    private fun getDispositionId(dispositionName : String) {
        casesViewModel.getDispositionIdFromRoomDB(dispositionName)
    }

    private fun setListeners() {
        binding.caseSummaryCloseBtn.setOnClickListener {
            dismiss()
        }
        binding.caseSummaryTotalCaseBtn.setOnClickListener {
            val actions =
                CaseSummaryBottomSheetDirections.actionNavigationHomeSummaryToNavigationCases(
                    "", "", "", "", fromDate,
                    toDate, true
                )
            findNavController().navigate(actions)
            dismiss()
        }

        binding.caseSummaryPtpBtn.setOnClickListener {
            getDispositionId(binding.ptpTv.text.toString())
        }
        binding.caseSummaryRtpBtn.setOnClickListener {
            val rtp = "Denial/RTP (Refused to Pay)"
            getDispositionId(rtp)
        }
        binding.caseSummaryBrokenPtpBtn.setOnClickListener {
            getDispositionId(binding.brokenPtpTv.text.toString())
        }
        binding.caseSummaryDisputeBtn.setOnClickListener {
            getDispositionId(binding.disputeTv.text.toString())
        }
        binding.caseSummaryCustomerNotFoundBtn.setOnClickListener {
            getDispositionId(binding.customerNotFoundTv.text.toString())
        }
        binding.caseSummaryCallbackBtn.setOnClickListener {
            getDispositionId(binding.callBackTv.text.toString())
        }
        binding.caseSummaryCollectBtn.setOnClickListener {
            getDispositionId(binding.collectTv.text.toString())
        }
        binding.caseSummaryPartiallyCollectBtn.setOnClickListener {
            getDispositionId(binding.partiallyCollectTv.text.toString())
        }
        binding.caseSummarySettlementBtn.setOnClickListener {
            getDispositionId(binding.settlementTv.text.toString())
        }
        binding.caseSummaryCustomerDeceasedBtn.setOnClickListener {
            getDispositionId(binding.customerDeceasedTv.text.toString())
        }
    }

    private fun updateUI() {
        //Date arguments received from Home Fragment according to selected Today, Week & Month Radio Button
        fromDate = caseArgs.fromDate
        toDate = caseArgs.toDate

        binding.totalCasesTv.text = summaryData.totalCases.toString()
        binding.ptpValueTv.text = summaryData.promiseToPay.toString()
        binding.rtpValueTv.text = summaryData.denialRTP.toString()
        binding.brokenPtpValueTv.text = summaryData.brokenPTP.toString()
        binding.disputeValueTv.text = summaryData.dispute.toString()
        binding.customerNotFoundValueTv.text = summaryData.customerNotFound.toString()
        binding.callBackValueTv.text = summaryData.callBack.toString()
        binding.collectValueTv.text = summaryData.collect.toString()
        binding.partiallyCollectValueTv.text = summaryData.partiallyCollect.toString()
        binding.settlementValueTv.text = summaryData.settlementForeclosure.toString()
        binding.customerDeceasedValueTv.text = summaryData.customerDeceased.toString()

    }

    private fun navigateToCasesScreen(dispositionId : Int) {
        val actions = CaseSummaryBottomSheetDirections.actionNavigationHomeSummaryToNavigationCases(
            dispositionId.toString(), "", "", "", fromDate,
            toDate, true
        )
        findNavController().navigate(actions)
        dismiss()
    }


}