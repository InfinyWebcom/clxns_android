package com.clxns.app.ui.home.account

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.clxns.app.R
import com.clxns.app.databinding.FragmentMyProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MyProfileFragment : Fragment() {

    private lateinit var myProfileViewModel: MyProfileViewModel
    lateinit var ctx: Context
    private lateinit var activityMyProfileBinding: FragmentMyProfileBinding
    private val bankNames = arrayOf("Kotak Bank", "SBI", "HDFC")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setInit()
        return activityMyProfileBinding.root
    }

    private fun setInit() {
        ctx = requireActivity()
        activityMyProfileBinding = FragmentMyProfileBinding.inflate(layoutInflater)

        val myProfileRepo = MyProfileRepo()
        val myProfileVMFactory = MyProfileVMFactory((requireActivity()).application, myProfileRepo)
        myProfileViewModel =
            ViewModelProvider(this, myProfileVMFactory).get(MyProfileViewModel::class.java)

        activityMyProfileBinding.profileEditBankBtn.setOnClickListener {
            val popup = PopupMenu(ctx, it)
            popup.menuInflater.inflate(R.menu.account_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->

                when (item.title) {
                    "Account" -> {
                    }
                    "Change Password" -> {
                    }
                    "Select Bank" -> {
                        val bankDialogBuilder = MaterialAlertDialogBuilder(ctx)
                            .setTitle("Select your bank")
                            .setItems(bankNames) { _: DialogInterface, item: Int ->
                                when (bankNames[item]) {
                                    "SBI" -> activityMyProfileBinding.selectedBankImage.setImageDrawable(
                                        ContextCompat.getDrawable(ctx, R.drawable.ic_state_bank_of_india_logo)
                                    )
                                    "Kotak Bank"
                                    -> activityMyProfileBinding.selectedBankImage.setImageDrawable(
                                        ContextCompat.getDrawable(ctx, R.drawable.ic_kotak_mahindra_bank)
                                    )
                                    "HDFC" -> activityMyProfileBinding.selectedBankImage.setImageDrawable(
                                        ContextCompat.getDrawable(ctx, R.drawable.ic_hdfc_bank_logo)
                                    )
                                }
                            }
                        bankDialogBuilder.show()
                    }
                }

                true
            }
            popup.show()
        }

    }
}