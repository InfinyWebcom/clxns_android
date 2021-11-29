package com.clxns.app.ui.login.forgotPassword

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.clxns.app.R
import com.google.android.material.textfield.TextInputEditText

class OTPTextWatcher(private val editText: Array<TextInputEditText>, private val view: View) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val otp = s.toString()
        when (view.id){
            R.id.otpET1 -> {
                if (otp.length == 1) {
                    editText[1].requestFocus()
                }
            }
            R.id.otpET2 -> {
                if (otp.length == 1)
                    editText[2].requestFocus()
                else if (otp.isEmpty())
                    editText[0].requestFocus()
            }
            R.id.otpET3 -> {
                if (otp.length == 1)
                    editText[3].requestFocus()
                else if (otp.isEmpty())
                    editText[1].requestFocus()
            }
            R.id.otpET4 -> {
                if (otp.isEmpty())
                    editText[2].requestFocus()
            }
        }
    }
}