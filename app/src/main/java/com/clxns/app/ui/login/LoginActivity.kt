package com.clxns.app.ui.login

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Pair
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityLoginBinding
import com.clxns.app.ui.login.forgotPassword.ForgotPasswordActivity
import com.clxns.app.ui.main.MainActivity
import com.clxns.app.utils.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding

    private lateinit var loginBtn: MaterialButton
    private lateinit var emailET: TextInputEditText
    private lateinit var emailIL: TextInputLayout
    private lateinit var passwordET: TextInputEditText
    private lateinit var passwordIL: TextInputLayout

    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        subscribeObserver()

        setListeners()

    }

    private fun initView() {
        loginBtn = binding.loginBtn
        emailET = binding.emailET
        emailIL = binding.emailIL
        passwordET = binding.passwordET
        passwordIL = binding.passwordIL

        /**
         * If a user has logout from the app then we'll fetch the last email address from the local storage
         * to remind the user last logged in email id.
         */
        val emailId = sessionManager.getString(Constants.USER_EMAIL)
        if (!emailId.isNullOrEmpty()) {
            binding.emailET.setText(emailId)
        }
    }

    private fun setListeners() {

        binding.txtForgetPassword.setOnClickListener {
            val goToFP = Intent(this, ForgotPasswordActivity::class.java)
            val p = Pair<View, String>(binding.loginLogo, "app_logo_anim")
            val options = ActivityOptions.makeSceneTransitionAnimation(this, p)
            startActivity(goToFP, options.toBundle())
        }
        loginBtn.setOnClickListener {
            this.hideKeyboard(binding.root)
            emailET.removeFocus()
            passwordET.removeFocus()

            if (!emailET.text.isNullOrEmpty() && !passwordET.text.isNullOrEmpty()) {
                loginViewModel.performLogin(
                    emailET.text.toString(),
                    passwordET.text.toString()
                )
            }
        }


        emailET.afterTextChanged {
            loginViewModel.loginDataChanged(
                emailET.text.toString()
            )
        }

        passwordET.afterTextChanged {
            loginViewModel.loginDataChanged(
                emailET.text.toString()
            )
        }

        passwordET.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> passwordET.removeFocus()
            }
            false
        }


    }

    private fun subscribeObserver() {

        loginViewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    loginBtn.isEnabled = true
                    loginBtn.text = getString(R.string.login)
                    Toast.makeText(this, response.data?.title, Toast.LENGTH_LONG).show()
                    if (response.data?.error == false) {
                        val loginData = response.data.loginData
                        val name = loginData?.firstName + " " + loginData?.lastName
                        binding.txtLogin.text = "Welcome $name"
                        sessionManager.saveAnyData(Constants.TOKEN, response.data.token!!)
                        sessionManager.saveAnyData(Constants.USER_NAME, name)
                        sessionManager.saveAnyData(Constants.USER_ID, loginData!!.id)
                        sessionManager.saveAnyData(Constants.USER_EMPLOYEE_ID, loginData.employeeId)
                        sessionManager.saveAnyData(Constants.USER_DOB, loginData.dob)
                        sessionManager.saveAnyData(Constants.USER_BLOOD_GROUP, loginData.bloodGroup)
                        sessionManager.saveAnyData(Constants.USER_EMAIL, loginData.email)
                        sessionManager.saveAnyData(Constants.USER_MOBILE, loginData.phone)
                        sessionManager.saveAnyData(Constants.USER_ADDRESS, loginData.address)
                        sessionManager.saveAnyData(Constants.USER_LOCATION, loginData.location)
                        if (!loginData.profileImage.isNullOrEmpty()) {
                            sessionManager.saveAnyData(
                                Constants.USER_IMAGE,
                                Constants.PROFILE_IMAGE_URL + loginData.profileImage
                            )
                        }
                        sessionManager.saveAnyData(Constants.IS_USER_LOGGED_IN, true)
                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }, Constants.SPLASH_SCREEN_TIMEOUT)
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    // show error message
                    loginBtn.isEnabled = true
                    loginBtn.text = getString(R.string.login)
                    binding.root.snackBar(response.message!!)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                    loginBtn.isEnabled = false
                    loginBtn.text = getString(R.string.please_wait)
                }
            }
        }

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

            if (loginState.emailAddressError != null) {
                emailIL.error = getString(loginState.emailAddressError)
            } else {
                emailIL.isErrorEnabled = false
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

