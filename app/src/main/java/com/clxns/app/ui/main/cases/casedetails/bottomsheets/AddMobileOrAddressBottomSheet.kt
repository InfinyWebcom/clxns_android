package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clxns.app.databinding.FragmentAddMobileAddressBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddMobileOrAddressBottomSheet : BottomSheetDialogFragment() {

    lateinit var ctx: Context
    lateinit var sheetDialogBinding: FragmentAddMobileAddressBottomSheetDialogBinding
    private var isMobile = false


    companion object {
        const val TAG = "AddMobileOrAddressBottomSheet"
    }

    fun newInstance(): AddMobileOrAddressBottomSheet {
        return AddMobileOrAddressBottomSheet()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isMobile = arguments?.getBoolean("isMobile") ?: false
        setInit()
        return sheetDialogBinding.root
    }

    private fun setInit() {
        ctx = requireActivity()
        sheetDialogBinding =
            FragmentAddMobileAddressBottomSheetDialogBinding.inflate(layoutInflater)

        sheetDialogBinding.updateDetailsBtn.setOnClickListener {
            this.dismiss()
        }
        if (isMobile){
            sheetDialogBinding.addNewMobileTxt.visibility = View.VISIBLE
            sheetDialogBinding.edtNewMobileNo.visibility = View.VISIBLE
            sheetDialogBinding.addNewAddressTxt.visibility = View.GONE
            sheetDialogBinding.edtNewAddress.visibility = View.GONE
        }


    }


}