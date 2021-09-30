package com.clxns.app.ui.main.account.changePassword

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.clxns.app.R
import com.clxns.app.data.api.helper.NetworkResult
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivityChangePasswordBinding
import com.clxns.app.ui.login.LoginActivity
import com.clxns.app.utils.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    private lateinit var newPasswordET: TextInputEditText
    private lateinit var confirmPasswordET: TextInputEditText
    private lateinit var oldPasswordET: TextInputEditText
    private lateinit var oldPasswordIL: TextInputLayout
    private lateinit var confirmPasswordIL: TextInputLayout

    private var isFromOTPScreen: Boolean = false

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        setObserver()

        setListeners()

    }

    private fun setListeners() {

        binding.changePasswordSubmitBtn.setOnClickListener {
            this.hideKeyboard(binding.root)
            removeFocusFromAllET()
            if (oldPasswordIL.isVisible) {
                if (!oldPasswordET.text.isNullOrBlank() && !newPasswordET.text.isNullOrBlank() &&
                    !confirmPasswordET.text.isNullOrBlank()) {
                    if (newPasswordET.text.toString()
                            .contentEquals(confirmPasswordET.text.toString())
                    ) {
                        changePasswordViewModel.changePassword(
                            token,
                            newPasswordET.text.toString(),
                            confirmPasswordET.text.toString(),
                            oldPasswordET.text.toString()
                        )
                        binding.progressBar.show()
                    } else {
                        binding.root.snackBar(getString(R.string.error_password_not_match))
                    }
                }else{
                    binding.root.snackBar("Fields cannot be empty")
                }
            } else {
                if (!newPasswordET.text.isNullOrBlank() && !confirmPasswordET.text.isNullOrBlank()) {
                    if (newPasswordET.text.toString()
                            .contentEquals(confirmPasswordET.text.toString())
                    ) {
                        changePasswordViewModel.changePassword(
                            token,
                            newPasswordET.text.toString(),
                            confirmPasswordET.text.toString(),
                            ""
                        )
                    } else {
                        binding.root.snackBar(getString(R.string.error_password_not_match))
                    }
                }else{
                    binding.root.snackBar("Fields cannot be empty")
                }
            }

        }

        newPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (confirmPasswordET.text.toString().isNotEmpty()) {
                    if (s.toString() == confirmPasswordET.text.toString()) {
                        setConfirmETTick()
                    } else {
                        setConfirmETCross()
                    }
                }
            }

        })

        confirmPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (newPasswordET.text.toString().isNotEmpty()) {
                    if (s.toString() == newPasswordET.text.toString()) {
                        setConfirmETTick()
                    } else {
                        setConfirmETCross()
                    }
                }
            }

        })

        binding.changePasswordBackBtn.setOnClickListener { onBackPressed() }


        binding.confirmPasswordET.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> confirmPasswordET.removeFocus()
            }
            false
        }
    }

    //If new password and confirm password matches then set the tick icon
    private fun setConfirmETTick() {
        confirmPasswordIL.startIconDrawable =
            AppCompatResources.getDrawable(applicationContext, R.drawable.ic_tick)
        confirmPasswordIL.setStartIconTintList(
            AppCompatResources.getColorStateList(applicationContext, R.color.colorPrimary)
        )
    }

    //If new password and confirm password does not matches then set the cross icon
    private fun setConfirmETCross() {
        confirmPasswordIL.startIconDrawable =
            AppCompatResources.getDrawable(applicationContext, R.drawable.ic_round_close_24)
        confirmPasswordIL.setStartIconTintList(
            AppCompatResources.getColorStateList(applicationContext, R.color.colorPrimary)
        )
    }

    override fun onResume() {
        super.onResume()
        if (isFromOTPScreen) {
            oldPasswordIL.visibility = View.GONE
        }
    }

    private fun initViews() {
        newPasswordET = binding.newPasswordET
        oldPasswordET = binding.oldPasswordET
        confirmPasswordET = binding.confirmPasswordET
        oldPasswordIL = binding.oldPasswordIL
        confirmPasswordIL = binding.confirmPasswordIL

        token = sessionManager.getString(Constants.TOKEN).toString()
        isFromOTPScreen = intent.getBooleanExtra("isFromOTPScreen", false)
    }

    private fun setObserver() {
        changePasswordViewModel.changePasswordResponse.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    toast(it.data?.title!!)
                    if (!it.data.error) {
                        if (isFromOTPScreen) {
                            //clear token
                            sessionManager.saveAnyData(Constants.TOKEN, "")
                            sessionManager.saveAnyData(Constants.IS_USER_LOGGED_IN, false)
                            //start login screen
                            val finishAllActivitiesExceptLogin =
                                Intent(this, LoginActivity::class.java)
                            finishAllActivitiesExceptLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(finishAllActivitiesExceptLogin)
                        } else {
                            sessionManager.saveAnyData(Constants.TOKEN, it.data.token!!)
                            onBackPressed()
                        }
                    }
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    // show error message
                    binding.progressBar.hide()
                    toast(it.message!!)
                }
                is NetworkResult.Loading -> {
                    // show a progress bar
                    binding.progressBar.show()
                    binding.root.snackBar("Changing password...")
                }
            }
        }
    }

    private fun removeFocusFromAllET() {
        newPasswordET.removeFocus()
        confirmPasswordET.removeFocus()
        oldPasswordET.removeFocus()
    }

}