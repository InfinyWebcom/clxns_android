package com.clxns.app.ui.login.forgotPassword

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityForgotPasswordBinding
import com.clxns.app.ui.main.account.changePassword.ChangePasswordActivity
import com.clxns.app.utils.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityForgotPasswordBinding

    private val viewModel : ForgotPasswordViewModel by viewModels()

    private lateinit var otpET1 : TextInputEditText
    private lateinit var otpET2 : TextInputEditText
    private lateinit var otpET3 : TextInputEditText
    private lateinit var otpET4 : TextInputEditText
    private lateinit var emailET : TextInputEditText

    private lateinit var didNotGetOTPTxtLayout : LinearLayout

    private lateinit var getOTPBtn : MaterialButton

    private lateinit var otpExpirationTimer : CountDownTimer

    @Inject
    lateinit var sessionManager : SessionManager
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        initView()

        setListener()

        setObserver()

    }

    private fun setObserver() {
        viewModel.responseGetOTP.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.root.snackBar(response.data?.title!!)

                    // bind data to the view
                    if (!response.data.error) {
                        sessionManager.saveAnyData(Constants.TOKEN, response.data.token.toString())
                        updateUIOnSuccessfulGetOTP()
                    }
                }
                is NetworkResult.Error -> {
                    binding.root.snackBar(response.message!!)
                }
                is NetworkResult.Loading -> {
                    binding.root.snackBar("Getting OTP...")
                }
            }
        }

        viewModel.responseVerifyOTP.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    toast(response.data?.title!!)
                    if (!response.data.error) {
                        sessionManager.saveAnyData(Constants.TOKEN, response.data.token!!)
                        val goToChangePasswordActivity =
                            Intent(this, ChangePasswordActivity::class.java)
                        goToChangePasswordActivity.putExtra("isFromOTPScreen", true)
                        startActivity(goToChangePasswordActivity)
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.root.snackBar(response.message!!)
                }
                is NetworkResult.Loading -> {
                    binding.root.snackBar("Verifying OTP...")
                }
            }
        }

    }

    private fun updateUIOnSuccessfulGetOTP() {
        if (didNotGetOTPTxtLayout.isVisible) {
            otpExpirationTimer.start()
            binding.resendOtpBtn.isClickable = false
        }
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
        emailET.hide()
        binding.didNotGetOTPTxtLayout.show()
        binding.otpETParent.show()
        getOTPBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = binding.didNotGetOTPTxtLayout.id
        }
        getOTPBtn.text = getString(R.string.verify_proceed)
        binding.emailLabel.text = getString(R.string.enter_otp)
        binding.forgotPasswordTitle.text = getString(R.string.verify_otp)
    }

    override fun onResume() {
        super.onResume()
        if (binding.forgotPasswordTitle.text.equals("Verify OTP")) {
            emailET.hide()
        }
    }

    private fun initView() {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        otpET1 = binding.otpET1
        otpET2 = binding.otpET2
        otpET3 = binding.otpET3
        otpET4 = binding.otpET4
        didNotGetOTPTxtLayout = binding.didNotGetOTPTxtLayout
        emailET = binding.forgotPasswordEmailET

        getOTPBtn = binding.getOTPBtn

        val edit = arrayOf(otpET1, otpET2, otpET3, otpET4)

        otpET1.addTextChangedListener(OTPTextWatcher(edit, otpET1))
        otpET2.addTextChangedListener(OTPTextWatcher(edit, otpET2))
        otpET3.addTextChangedListener(OTPTextWatcher(edit, otpET3))
        otpET4.addTextChangedListener(OTPTextWatcher(edit, otpET4))


        val resendOtpTxt = getString(R.string.resent_otp)
        binding.resendOtpBtn.text = resendOtpTxt

        //Initializing count down timer for resend otp button
        otpExpirationTimer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished : Long) {
                val secondsInMilli : Long = 1000
                val minutesInMilli = secondsInMilli * 60
                var diff = millisUntilFinished
                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli
                val elapsedSeconds = diff / secondsInMilli
                val timeLeft = "$elapsedMinutes:$elapsedSeconds"
                binding.resendOtpBtn.text = timeLeft
            }

            override fun onFinish() {
                binding.resendOtpBtn.text = resendOtpTxt
                binding.resendOtpBtn.isClickable = true
            }

        }
    }


    private fun setListener() {
        binding.forgotPasswordBackBtn.setOnClickListener { onBackPressed() }

        binding.resendOtpBtn.setOnClickListener {
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
            if (getOTPBtn.text.equals(getString(R.string.get_otp))) {
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

        otpET4.setOnEditorActionListener { _, actionId, _ ->
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
        exitDialog.setMessage("Are you sure you want to exit the \"OTP Verification Process\"?")

        exitDialog.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            finish()
        }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val d = exitDialog.create()
        d.show()
        d.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
        d.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(this, R.color.light_red))
        d.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }

    private fun clearFocusFromOTPET() {
        otpET1.removeFocus()
        otpET2.removeFocus()
        otpET3.removeFocus()
        otpET4.removeFocus()
    }
}