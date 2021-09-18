package com.clxns.app.ui.home.account

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clxns.app.R
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentMyProfileBinding
import com.clxns.app.ui.changePassword.ChangePasswordActivity
import com.clxns.app.utils.Constants
import com.clxns.app.utils.snackBar
import com.clxns.app.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val profileVM: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentMyProfileBinding
    private val bankNames = arrayOf("Kotak Bank", "SBI", "HDFC")

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMyProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }


    private fun bindUI() {
        binding.userNameTv.text = sessionManager.getString(Constants.USER_NAME)
        binding.userEmployeeCodeTv.text =
            "Employee Code : " + sessionManager.getString(Constants.USER_EMPLOYEE_ID)
        binding.userEmployeeBloodGroupTv.text =
            "Blood Group : " + sessionManager.getString(Constants.USER_BLOOD_GROUP)
//        binding.reportingManagerTv.text="Reporting Manager : "+sessionManager.getString(Constants.USER_BLOOD_GROUP)
//        binding.managerContactTv.text="Manager Contact : "+sessionManager.getString(Constants.USER_BLOOD_GROUP)
        binding.profileEditBankBtn.setOnClickListener {
            onMenuClick(it)
        }

    }

    private fun onMenuClick(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.account_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->

            when (item.title) {
                "Account" -> {
                    binding.root.snackBar("Refreshing...")
                }
                "Change Password" -> {
                    startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
                }
                "Logout" -> {
                    requireContext().toast("Logout")
                }
                "Select Bank" -> {
                    val bankDialogBuilder = MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select your bank")
                        .setItems(bankNames) { _: DialogInterface, i: Int ->
                            when (bankNames[i]) {
                                "SBI" -> binding.selectedBankImage.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_state_bank_of_india_logo
                                    )
                                )
                                "Kotak Bank"
                                -> binding.selectedBankImage.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_kotak_mahindra_bank
                                    )
                                )
                                "HDFC" -> binding.selectedBankImage.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_hdfc_bank_logo
                                    )
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