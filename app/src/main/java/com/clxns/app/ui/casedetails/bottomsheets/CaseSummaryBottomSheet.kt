package com.clxns.app.ui.casedetails.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.clxns.app.databinding.CasesSummaryLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CaseSummaryBottomSheet(private val callback: OnClick) : BottomSheetDialogFragment() {

    lateinit var casesSummaryLayoutBinding: CasesSummaryLayoutBinding
    lateinit var casesSummaryView: View

    companion object {
        const val TAG = "CaseSummaryBottomSheet"


        fun newInstance(callback: OnClick): CaseSummaryBottomSheet {
            return CaseSummaryBottomSheet(callback)
        }
    }

    interface OnClick {
        fun onClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        casesSummaryLayoutBinding = CasesSummaryLayoutBinding.inflate(layoutInflater)
        casesSummaryView = casesSummaryLayoutBinding.root
        return casesSummaryView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        casesSummaryLayoutBinding.caseSummaryTotalCaseBtn.setOnClickListener {
            Toast.makeText(context, "Showing result for total cases", Toast.LENGTH_LONG).show()
            this.dismiss()
            callback.onClick()
        }

        casesSummaryLayoutBinding.caseSummaryCloseBtn.setOnClickListener {
            this.dismiss()
        }

        casesSummaryLayoutBinding.totalCasesBtn1.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn2.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn3.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn4.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn5.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn6.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn7.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn8.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn9.setOnClickListener {
            button()
        }
        casesSummaryLayoutBinding.totalCasesBtn10.setOnClickListener {
            button()
        }

    }

    fun button() {
        this.dismiss()
        callback.onClick()
    }


}