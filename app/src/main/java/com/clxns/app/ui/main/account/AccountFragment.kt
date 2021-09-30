package com.clxns.app.ui.main.account

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.FragmentAccountBinding
import com.clxns.app.ui.login.LoginActivity
import com.clxns.app.ui.main.account.changePassword.ChangePasswordActivity
import com.clxns.app.utils.Constants
import com.clxns.app.utils.loadImage
import com.clxns.app.utils.snackBar
import com.clxns.app.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {

    private val accountViewModel: AccountViewModel by viewModels()
    private lateinit var binding: FragmentAccountBinding
    private var bankNames = arrayOf<String>()

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        accountViewModel.getUserDetails(sessionManager.getString(Constants.TOKEN)!!)
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountViewModel.getBankNameList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        subscribeObserver()
        setListeners()
        setBankImage()
    }

    private fun setListeners() {
        binding.profileMoreOptionBtn.setOnClickListener {
            onMenuClick(it)
        }
    }


    private fun initViews() {
        val userName = sessionManager.getString(Constants.USER_NAME)
        val managerName =
            "Reporting Manager : " + sessionManager.getString(Constants.USER_REPORTING_MANAGER)
        val userBloodGroup = "Blood Group : " + sessionManager.getString(Constants.USER_BLOOD_GROUP)
        val userEmployeeCode =
            "Employee Code : " + sessionManager.getString(Constants.USER_EMPLOYEE_ID)
        val managerContact =
            "Manager Contact : " + sessionManager.getString(Constants.USER_REPORTING_MANAGER_CONTACT)
        binding.userNameTv.text = userName
        binding.userEmployeeCodeTv.text = userEmployeeCode
        binding.userEmployeeBloodGroupTv.text = userBloodGroup
        binding.reportingManagerTv.text = managerName
        binding.managerContactTv.text = managerContact

        if (!sessionManager.getString(Constants.USER_IMAGE).isNullOrEmpty()) {
            val imageUrl = sessionManager.getString(Constants.USER_IMAGE)
            binding.userProfileImg.loadImage(imageUrl!!)
        } else {
            val names = sessionManager.getString(Constants.USER_NAME)?.split(" ")
            val name = names?.get(0)?.substring(0, 1) + names?.get(1)?.substring(0, 1)
            binding.userNameInitials.visibility = View.VISIBLE
            binding.userNameInitials.text = name
        }
    }

    private fun setBankImage() {
        val bankImageUrl = sessionManager.getString(Constants.SELECTED_BANK)
        if (!bankImageUrl.isNullOrEmpty()) {
            binding.selectedBankImage.loadImage(bankImageUrl)
        }
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
                            accountViewModel.getBankImage(bankNames[i])
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
        logoutDialog.setTitle("Confirm Logout")
        logoutDialog.setMessage("Are you sure you want to logout?")

        logoutDialog.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            accountViewModel.logout(sessionManager.getString(Constants.TOKEN)!!)
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = logoutDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun subscribeObserver() {
        accountViewModel.responseUserDetails.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data?.error == false) {
                        val loginData = it.data.loginData
                        val name = loginData?.firstName + " " + loginData?.lastName
                        val managerName =
                            loginData?.reportingDetails?.firstName + " " + loginData?.reportingDetails?.lastName
                        sessionManager.saveAnyData(Constants.USER_NAME, name)
                        sessionManager.saveAnyData(
                            Constants.USER_EMPLOYEE_ID,
                            loginData?.employeeId!!
                        )
                        if (!loginData.profileImage.isNullOrEmpty()) {
                            sessionManager.saveAnyData(
                                Constants.USER_IMAGE,
                                Constants.PROFILE_IMAGE_URL + loginData.profileImage
                            )
                        }
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
                    binding.root.snackBar(it.message!!)
                }

                is NetworkResult.Loading -> {
                }
            }
        }

        accountViewModel.responseLogout.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    // update UI
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
                    // show error message
                    binding.root.snackBar(response.message!!)
                }
                is NetworkResult.Loading -> {
                    binding.root.snackBar("Logging out...")
                }
            }
        }

        accountViewModel.responseBankNames.observe(viewLifecycleOwner) {
            bankNames = it.toTypedArray()
        }

        accountViewModel.responseBankImage.observe(viewLifecycleOwner) {
            sessionManager.saveAnyData(Constants.SELECTED_BANK, it)
            setBankImage()
        }
    }
}