package com.clxns.app.ui.home.account

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentMyProfileBinding
import com.clxns.app.ui.changePassword.ChangePasswordActivity
import com.clxns.app.ui.login.LoginActivity
import com.clxns.app.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentMyProfileBinding
    private val bankNames = arrayOf("Kotak Bank", "SBI", "HDFC")

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        profileViewModel.getUserDetails(sessionManager.getString(Constants.TOKEN)!!)
        binding = FragmentMyProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initViews()
        subscribeObserver()
        setListeners()
    }

    private fun setListeners() {
        binding.profileEditBankBtn.setOnClickListener {
            onMenuClick(it)
        }
    }


    private fun initViews() {
        val userName = sessionManager.getString(Constants.USER_NAME)
        val managerName =
            "Reporting Manager : $sessionManager.getString(Constants.USER_REPORTING_MANAGER)"
        val userBloodGroup = "Blood Group : $sessionManager.getString(Constants.USER_BLOOD_GROUP)"
        val userEmployeeCode =
            "Employee Code : $sessionManager.getString(Constants.USER_EMPLOYEE_ID)"
        val managerContact =
            "Manager Contact : $sessionManager.getString(Constants.USER_REPORTING_MANAGER_CONTACT)"
        binding.userNameTv.text = userName
        binding.userEmployeeCodeTv.text = userEmployeeCode
        binding.userEmployeeBloodGroupTv.text = userBloodGroup
        binding.reportingManagerTv.text = managerName
        binding.managerContactTv.text = managerContact


    }

    private fun onMenuClick(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.account_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->

            when (item.title) {
                "Change Password" -> {
                    startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
                }
                "Logout" -> {
                    showConfirmLogoutDialog()
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

    private fun showConfirmLogoutDialog() {
        val logoutDialog = AlertDialog.Builder(requireContext())
        logoutDialog.setTitle("Logout/")
        logoutDialog.setMessage("Are you sure want to logout?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            profileViewModel.logout(sessionManager.getString(Constants.TOKEN)!!)
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun subscribeObserver() {
        profileViewModel.responseUserDetails.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (it.data?.error == false) {
                        val loginData = it.data.loginData
                        val name = loginData?.firstName + " " + loginData?.lastName
                        val managerName =
                            loginData?.reportingDetails?.firstName + " " + loginData?.reportingDetails?.lastName
                        sessionManager.saveAnyData(Constants.TOKEN, it.data.token!!)
                        sessionManager.saveAnyData(Constants.USER_NAME, name)
                        sessionManager.saveAnyData(
                            Constants.USER_EMPLOYEE_ID,
                            loginData?.employeeId!!
                        )
                        sessionManager.saveAnyData(Constants.USER_EMAIL, loginData.email)
                        sessionManager.saveAnyData(Constants.USER_MOBILE, loginData.phone)
                        sessionManager.saveAnyData(Constants.USER_ADDRESS, loginData.address)
                        sessionManager.saveAnyData(Constants.USER_LOCATION, loginData.location)
                        sessionManager.saveAnyData(Constants.USER_REPORTING_MANAGER, managerName)
                        sessionManager.saveAnyData(
                            Constants.USER_REPORTING_MANAGER_CONTACT,
                            loginData.reportingDetails?.phone!!
                        )
                    }
                    initViews()
                }

                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    binding.root.snackBar(it.message!!)
                }

                is NetworkResult.Loading -> binding.progressBar.show()
            }
        }

        profileViewModel.responseLogout.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    // update UI
                    binding.progressBar.hide()
                    requireContext().toast(response.data?.title!!)
                    if (response.data.error == false) {
                        //clear token
                        sessionManager.saveAnyData(Constants.TOKEN, "")
                        sessionManager.saveAnyData(Constants.IS_USER_LOGGED_IN, false)
                        //start login screen
                        val intent = Intent(requireActivity(), LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    requireContext().toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    // show a progress bar
                }
            }
        }
    }
}