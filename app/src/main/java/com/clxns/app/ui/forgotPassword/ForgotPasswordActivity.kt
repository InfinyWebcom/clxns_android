package com.clxns.app.ui.forgotPassword

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityForgotPasswordBinding
import com.clxns.app.ui.changePassword.ChangePasswordActivity
import com.clxns.app.ui.login.LoginActivity
import com.clxns.app.utils.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    private val viewModel: ForgotPasswordViewModel by viewModels()

    private lateinit var otpET1: TextInputEditText
    private lateinit var otpET2: TextInputEditText
    private lateinit var otpET3: TextInputEditText
    private lateinit var otpET4: TextInputEditText
    private lateinit var emailET: TextInputEditText

    private lateinit var didNotGetOTPTxt: TextView

    private lateinit var getOTPBtn: MaterialButton

    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        setListener()

        setObserver()

    }

    private fun setObserver() {
        viewModel.responseGetOTP.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
//                    binding.progressBar.hide()

                    this.toast(response.data?.title!!)
                    sessionManager.saveAnyData(Constants.TOKEN, response.data.token.toString())
                    updateUIOnSuccessfulGetOTP()
                    // bind data to the view
                }
                is NetworkResult.Error -> {
//                    binding.progressBar.hide()
                    toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
//                    binding.progressBar.show()
                    binding.forgotPasswordSubHeader.text = "Loading...."
                    // show a progress bar
                }
            }
        }

        viewModel.responseVerifyOTP.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
//                    binding.progressBar.hide()

                    sessionManager.saveAnyData(Constants.TOKEN, response.data?.token!!)
                    this.toast(response.data.title)
                    val goToChangePasswordActivity =
                        Intent(this, ChangePasswordActivity::class.java)
                    goToChangePasswordActivity.putExtra("isFromOTPScreen", true)
                    startActivity(goToChangePasswordActivity)
                    // bind data to the view
                }
                is NetworkResult.Error -> {
//                    binding.progressBar.hide()
                    toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
//                    binding.progressBar.show()
                    binding.root.snackBar("Verifying OTP...")
                    // show a progress bar
                }
            }
        }

//        passwordViewModel.forgotPasswordResponse.observe(this, {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    this.toast(it.data?.title!!)
//                    sessionManager.saveAnyData(Constants.TOKEN, it.data.token.toString())
//                    updateUIOnSuccessfulGetOTP()
//                }
//                Status.ERROR -> {
//                    binding.root.snackBar(it.message!!)
//                }
//                Status.LOADING -> {
//                    binding.forgotPasswordSubHeader.text = "Loading...."
//                }
//            }
//        })



//        passwordViewModel.verifyOTPResponse.observe(this, {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    sessionManager.saveAnyData(Constants.TOKEN, it.data?.token!!)
//                    this.toast(it.data.title)
//                    val goToChangePasswordActivity =
//                        Intent(this, ChangePasswordActivity::class.java)
//                    goToChangePasswordActivity.putExtra("isFromOTPScreen", true)
//                    startActivity(goToChangePasswordActivity)
//                }
//                Status.ERROR -> {
//                    binding.root.snackBar(it.message!!)
//                }
//                Status.LOADING -> {
//                    binding.root.snackBar("Verifying OTP...")
//                }
//            }
//        })



    }

    private fun updateUIOnSuccessfulGetOTP() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.forgotPasswordSubHeader.text = (Html.fromHtml(
                "<html><body><font size=12><b>OTP Verification</b></font><br/>" +
                        "Enter the OTP sent to ${emailET.text.toString()}</body><html>",
                Html.FROM_HTML_MODE_LEGACY
            ))
        } else {
            binding.forgotPasswordSubHeader.text =
                Html.fromHtml(
                    "<html><body><font size=12><b>OTP Verification</b></font><br/>" +
                            "Enter the OTP sent to ${emailET.text.toString()}</body><html>"
                )
        }
        emailET.visibility = View.GONE
        binding.didNotGetOTPTxt.visibility = View.VISIBLE
        binding.otpETParent.visibility = View.VISIBLE
        getOTPBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = binding.didNotGetOTPTxt.id
        }
        getOTPBtn.text = getString(R.string.verify_proceed)
        binding.emailLabel.text = getString(R.string.enter_otp)
        binding.forgotPasswordTitle.text = getString(R.string.verify_otp)
    }

    override fun onResume() {
        super.onResume()
        if (binding.forgotPasswordTitle.text.equals("Verify OTP")) {
            emailET.visibility = View.GONE
        }
    }

    private fun initView() {

        otpET1 = binding.otpET1
        otpET2 = binding.otpET2
        otpET3 = binding.otpET3
        otpET4 = binding.otpET4
        didNotGetOTPTxt = binding.didNotGetOTPTxt
        emailET = binding.forgotPasswordEmailET

        getOTPBtn = binding.getOTPBtn

        val edit = arrayOf(otpET1, otpET2, otpET3, otpET4)

        otpET1.addTextChangedListener(OTPTextWatcher(edit, otpET1))
        otpET2.addTextChangedListener(OTPTextWatcher(edit, otpET2))
        otpET3.addTextChangedListener(OTPTextWatcher(edit, otpET3))
        otpET4.addTextChangedListener(OTPTextWatcher(edit, otpET4))

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            didNotGetOTPTxt.text = (Html.fromHtml(
                "<html><body>Didn't receive the OTP?<font size=12 color=red> <b>RESEND OTP</b></font></body><html>",
                Html.FROM_HTML_MODE_LEGACY
            ))
        } else {
            didNotGetOTPTxt.text =
                Html.fromHtml("<html><body>Didn't receive the OTP?<font size=12 color=red> <b>RESEND OTP</b></font></body><html>")
        }
    }


    private fun setListener() {
        binding.forgotPasswordBackBtn.setOnClickListener { onBackPressed() }

        binding.didNotGetOTPTxt.setOnClickListener {
            //Requesting the OTP again after not receiving
            viewModel.getOTP(emailET.text.toString())
            clearFocusFromOTPET()
            otpET1.setText("")
            otpET2.setText("")
            otpET3.setText("")
            otpET4.setText("")
        }

        getOTPBtn.setOnClickListener {
            this.hideKeyboard(binding.root)
            if (getOTPBtn.text.equals("Get OTP")) {
                emailET.removeFocus()
                if (emailET.text.toString().isNotEmpty() && emailET.text.toString()
                        .isValidEmail()
                ) {
                    viewModel.getOTP(emailET.text.toString())
                } else {
                    binding.root.snackBar("Please enter correct email address")
                }
            } else {
                if (otpET1.text?.isNotEmpty() == true && otpET2.text?.isNotEmpty() == true
                    && otpET3.text?.isNotEmpty() == true && otpET4.text?.isNotEmpty() == true
                ) {
                    val otp =
                        "${otpET1.text.toString()}${otpET2.text.toString()}${otpET3.text.toString()}${otpET4.text.toString()}"
                    viewModel.verifyOTP(
                        sessionManager.getString(Constants.TOKEN).toString(),
                        otp,
                        emailET.text.toString()
                    )
                }
                clearFocusFromOTPET()
            }

        }

        binding.otpET4.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> clearFocusFromOTPET()
            }
            false
        }

        emailET.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> emailET.removeFocus()
            }
            false
        }


    }

    override fun onBackPressed() {
        if (binding.forgotPasswordTitle.text.equals("Verify OTP")) {
            showExitConfirmDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showExitConfirmDialog() {
        val exitDialog = AlertDialog.Builder(this)
        exitDialog.setTitle("Confirm exit?")
        exitDialog.setMessage("Are you sure want to exit \"OTP Verification Process\"?")

        exitDialog.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            finish()
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = exitDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun clearFocusFromOTPET() {
        otpET1.removeFocus()
        otpET2.removeFocus()
        otpET3.removeFocus()
        otpET4.removeFocus()
    }
}