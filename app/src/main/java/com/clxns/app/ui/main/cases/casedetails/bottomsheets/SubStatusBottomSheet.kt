package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clxns.app.data.model.SubStatusModel
import com.clxns.app.databinding.FragmentSubStatusBottomSheetBinding
import com.clxns.app.ui.main.cases.casedetails.casestatus.checkin.SubStatusAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SubStatusBottomSheet : BottomSheetDialogFragment(),
    SubStatusAdapter.OnSubStatusActionListener {

    lateinit var subStatusBinding: FragmentSubStatusBottomSheetBinding

    companion object {
        const val TAG = "SubStatusBottomSheet"
    }

    fun newInstance(): SubStatusBottomSheet {
        return SubStatusBottomSheet()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        subStatusBinding = FragmentSubStatusBottomSheetBinding.inflate(layoutInflater)
        return subStatusBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ctx = requireActivity()

        val statusList = ArrayList<SubStatusModel>()
        statusList.add(SubStatusModel("House Locked"))
        statusList.add(SubStatusModel("Address Not Found"))
        statusList.add(SubStatusModel("Not Available but resides here"))
        statusList.add(SubStatusModel("Residence Shifted"))
        statusList.add(SubStatusModel("Left Message with the family member"))
        subStatusBinding.subStatusRV.adapter = SubStatusAdapter(ctx, statusList, this)
    }

    override fun openSubStatusActionBottomSheet() {
        val openActionSheet = SubStatusActionBottomSheet().newInstance()
        openActionSheet.show(childFragmentManager, SubStatusActionBottomSheet.TAG)
    }
}