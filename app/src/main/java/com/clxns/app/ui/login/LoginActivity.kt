package com.clxns.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.clxns.app.R
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityLoginBinding
import com.clxns.app.ui.MainActivity
import com.clxns.app.ui.forgotPassword.ForgotPasswordActivity
import com.clxns.app.utils.Constants
import com.clxns.app.utils.Status
import com.clxns.app.utils.Utilities
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var loginBinding: ActivityLoginBinding

    private lateinit var loginBtn: MaterialButton
    private lateinit var mobileNumberET: TextInputEditText
    private lateinit var mobileNumberIL: TextInputLayout
    private lateinit var passwordET: TextInputEditText
    private lateinit var passwordIL: TextInputLayout

    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        initView()

        setObserver()

        setListeners()

    }

    private fun initView() {
        loginBtn = loginBinding.loginBtn
        mobileNumberET = loginBinding.mobileNumberET
        mobileNumberIL = loginBinding.mobileNumberIL
        passwordET = loginBinding.passwordET
        passwordIL = loginBinding.passwordIL
    }

    private fun setListeners() {

        loginBinding.txtForgetPassword.setOnClickListener {
            startActivity(
                Intent(this, ForgotPasswordActivity::class.java)
            )
        }
        loginBtn.setOnClickListener {
            Utilities.hideKeyboardFrom(this, loginBinding.root)
            Utilities.clearFocus(mobileNumberET)
            Utilities.clearFocus(passwordET)

            if (!mobileNumberET.text.isNullOrEmpty() && !passwordET.text.isNullOrEmpty()) {
                loginViewModel.performLogin(
                    mobileNumberET.text.toString(),
                    passwordET.text.toString()
                )
            }
        }

        mobileNumberET.afterTextChanged {
            loginViewModel.loginDataChanged(
                mobileNumberET.text.toString(),
                passwordET.text.toString()
            )
        }

        passwordET.afterTextChanged {
            loginViewModel.loginDataChanged(
                mobileNumberET.text.toString(),
                passwordET.text.toString()
            )
        }

        passwordET.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> Utilities.clearFocus(passwordET)
            }
            false
        }


    }

    private fun setObserver() {

        loginViewModel.loginResponse.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    Toast.makeText(this, it.data?.title, Toast.LENGTH_LONG).show()
                    val loginData = it.data?.loginData
                    val name = loginData?.firstName + loginData?.lastName
                    loginBinding.txtLogin.text = "Welcome $name"
                    sessionManager.saveAnyData(Constants.TOKEN, it.data?.token!!)
                    sessionManager.saveAnyData(Constants.USER_NAME, name)
                    sessionManager.saveAnyData(Constants.USER_ID, loginData!!.id)
                    sessionManager.saveAnyData(Constants.USER_DOB, loginData.dob)
                    sessionManager.saveAnyData(Constants.USER_EMAIL, loginData.email)
                    sessionManager.saveAnyData(Constants.USER_MOBILE, loginData.phone)
                    sessionManager.saveAnyData(Constants.USER_ADDRESS, loginData.address)
                    sessionManager.saveAnyData(Constants.USER_LOCATION, loginData.location)
                    sessionManager.saveAnyData(Constants.IS_USER_LOGGED_IN, true)
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, Constants.SPLASH_SCREEN_TIMEOUT)
                }
                Status.ERROR -> {
                    loginBinding.txtLogin.text = it.message
                }
                Status.LOADING -> {
                    loginBinding.txtLogin.text = "Loading...."
                }
            }
        })

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            if (loginState.isDataValid) {
                loginBtn.isEnabled = true
                loginBtn.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.colorPrimary)
            } else {
                loginBtn.isEnabled = false
                loginBtn.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.quantum_grey400)
            }

            if (loginState.mobileNumberError != null) {
                mobileNumberIL.error = getString(loginState.mobileNumberError)
            } else {
                mobileNumberIL.isErrorEnabled = false
            }
            if (loginState.passwordError != null) {
                passwordIL.error = getString(loginState.passwordError)
            } else {
                passwordIL.isErrorEnabled = false
            }
        })

    }

    /**
     * Extension function to simplify setting an afterTextChanged action to EditText components.
     */
    private fun TextInputEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}

