package com.clxns.app.ui.main.cases.casedetails.bottomsheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clxns.app.R
import com.clxns.app.databinding.FragmentAddMobileAddressBottomSheetDialogBinding
import com.clxns.app.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddMobileOrAddressBottomSheet(private val listener: OnClick) : BottomSheetDialogFragment() {

    lateinit var ctx: Context
    lateinit var sheetDialogBinding: FragmentAddMobileAddressBottomSheetDialogBinding
    private var isMobile = false


    companion object {
        const val TAG = "AddMobileOrAddressBottomSheet"

        fun newInstance(listener: OnClick): AddMobileOrAddressBottomSheet {
            return AddMobileOrAddressBottomSheet(listener)
        }
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
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
            if (validate()) {
                if (isMobile) {
                    listener.onMobileClick(sheetDialogBinding.edtNewMobileNo.text.toString())
                } else {
                    listener.onAddressClick(sheetDialogBinding.edtNewAddress.text.toString())
                }
                this.dismiss()
            }
        }
        if (isMobile) {
            sheetDialogBinding.addNewMobileTxt.visibility = View.VISIBLE
            sheetDialogBinding.edtNewMobileNo.visibility = View.VISIBLE
            sheetDialogBinding.addNewAddressTxt.visibility = View.GONE
            sheetDialogBinding.edtNewAddress.visibility = View.GONE
        }


    }

    private fun validate(): Boolean {
        if (isMobile) {
            if (sheetDialogBinding.edtNewMobileNo.text.toString().isEmpty() ||
                sheetDialogBinding.edtNewMobileNo.text.toString().isBlank()
            ) {
                context?.toast("Please enter mobile number")
                return false
            }

            if (sheetDialogBinding.edtNewMobileNo.text.toString().length!=10
            ) {
                context?.toast("Please enter valid mobile number")
                return false
            }
        } else {
            if (sheetDialogBinding.edtNewAddress.text.toString().isEmpty() ||
                sheetDialogBinding.edtNewAddress.text.toString().isBlank()
            ) {
                context?.toast("Please enter address")
                return false
            }
        }
        return true
    }

    interface OnClick {
        fun onMobileClick(mobile: String)
        fun onAddressClick(address: String)
    }


}