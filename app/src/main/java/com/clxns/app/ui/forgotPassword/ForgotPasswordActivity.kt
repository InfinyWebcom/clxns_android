package com.clxns.app.ui.forgotPassword

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.clxns.app.ui.MainActivity
import com.clxns.app.R
import com.clxns.app.databinding.ActivityForgotPasswordBinding
import com.clxns.app.utils.Constants
import com.clxns.app.utils.Status
import com.clxns.app.utils.Utilities
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    private val passwordViewModel: ForgotPasswordViewModel by viewModels()

    private lateinit var otpET1: TextInputEditText
    private lateinit var otpET2: TextInputEditText
    private lateinit var otpET3: TextInputEditText
    private lateinit var otpET4: TextInputEditText
    private lateinit var forgotPasswordMobileET: TextInputEditText

    private lateinit var didNotGetOTPTxt: TextView

    private lateinit var getOTPBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        setListener()

        setObserver()

    }

    private fun setObserver() {
        passwordViewModel.forgotPasswordResponse.observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    Toast.makeText(this, it.data?.title, Toast.LENGTH_LONG).show()
                    updateUIOnSuccessfulGetOTP()
                }
                Status.ERROR -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Status.LOADING -> {
                    binding.forgotPasswordSubHeader.text = "Loading...."
                }
            }
        })

        passwordViewModel.verifyOTPResponse.observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    Utilities.showSnackBar("OTP verification completed successfully", binding.root)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                Status.ERROR -> {
                    Utilities.showSnackBar(it.message, binding.root)
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
                Status.LOADING -> {

                }
            }
        })
    }

    private fun updateUIOnSuccessfulGetOTP() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.forgotPasswordSubHeader.text = (Html.fromHtml(
                "<html><body><font size=12><b>OTP Verification</b></font><br/>" +
                        "Enter the OTP sent to +91-${forgotPasswordMobileET.text.toString()}</body><html>",
                Html.FROM_HTML_MODE_LEGACY
            ))
        } else {
            binding.forgotPasswordSubHeader.text =
                Html.fromHtml(
                    "<html><body><b>OTP Verification</b><br/>" +
                            "Enter the OTP sent to +91-${forgotPasswordMobileET.text.toString()}</body><html>"
                )
        }
        forgotPasswordMobileET.visibility = View.GONE
        binding.didNotGetOTPTxt.visibility = View.VISIBLE
        binding.otpETParent.visibility = View.VISIBLE
        getOTPBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToBottom = binding.didNotGetOTPTxt.id
        }
        getOTPBtn.text = getString(R.string.verify_otp)
        binding.mobileNumberLabel.text = getString(R.string.enter_otp)
    }

    private fun initView() {

        otpET1 = binding.otpET1
        otpET2 = binding.otpET2
        otpET3 = binding.otpET3
        otpET4 = binding.otpET4
        didNotGetOTPTxt = binding.didNotGetOTPTxt
        forgotPasswordMobileET = binding.forgotPasswordMobileET

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
        binding.forgotPasswordBackBtn.setOnClickListener { finish() }

        binding.didNotGetOTPTxt.setOnClickListener {
            //Requesting the OTP again after not receiving
            passwordViewModel.getOTPFromDB(forgotPasswordMobileET.text.toString())
            clearFocusFromOTPET()
            otpET1.setText("")
            otpET2.setText("")
            otpET3.setText("")
            otpET4.setText("")
        }

        getOTPBtn.setOnClickListener {
            Utilities.hideKeyboardFrom(this, binding.root)
            if (getOTPBtn.text.equals("Get OTP")) {
                Utilities.clearFocus(forgotPasswordMobileET)
                if (forgotPasswordMobileET.text.toString()
                        .isNotEmpty() && forgotPasswordMobileET.text.toString().length == 10
                ) {
                    passwordViewModel.getOTPFromDB(binding.forgotPasswordMobileET.text.toString())
                } else {
                    Utilities.showSnackBar("Please enter correct mobile no.", binding.root)
                }
            } else {
                if (otpET1.text?.isNotEmpty() == true && otpET2.text?.isNotEmpty() == true
                    && otpET3.text?.isNotEmpty() == true && otpET4.text?.isNotEmpty() == true
                ) {
                    val otp = "${otpET1.text.toString()}${otpET2.text.toString()}${otpET3.text.toString()}${otpET4.text.toString()}"
                    Utilities.showSnackBar(otp, binding.root)
                    //passwordViewModel.verifyOTP(Constants.TOKEN, otp, Constants.DEMO_USER)
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


    }

    private fun clearFocusFromOTPET() {
        Utilities.clearFocus(otpET1)
        Utilities.clearFocus(otpET2)
        Utilities.clearFocus(otpET3)
        Utilities.clearFocus(otpET4)
    }
}