package com.clxns.app.ui.changePassword

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
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()

    private lateinit var newPasswordET: TextInputEditText
    private lateinit var confirmPasswordET: TextInputEditText
    private lateinit var oldPasswordET: TextInputEditText
    private lateinit var oldPasswordIL: TextInputLayout
    private lateinit var confirmPasswordIL: TextInputLayout

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

        token = sessionManager.getString(Constants.TOKEN).toString()

    }

    private fun setListeners() {

        binding.changePasswordSubmitBtn.setOnClickListener {
            this.hideKeyboard(binding.root)
            confirmPasswordET.removeFocus()
            if (oldPasswordIL.isVisible) {
                if (!oldPasswordET.text.isNullOrEmpty() && !newPasswordET.text.isNullOrEmpty() && !confirmPasswordET.text.isNullOrEmpty()) {
                    Timber.i("With old password")
                    if (newPasswordET.text.toString()
                            .contentEquals(confirmPasswordET.text.toString())
                    ) {
                        viewModel.changePassword(
                            token,
                            newPasswordET.text.toString(),
                            confirmPasswordET.text.toString(),
                            oldPasswordET.text.toString()
                        )
                    } else {
                        binding.root.snackBar(getString(R.string.error_password_not_match))
                    }
                }
            } else if (!newPasswordET.text.isNullOrEmpty() && !confirmPasswordET.text.isNullOrEmpty()) {
                Timber.i("Without old password")
                if (newPasswordET.text.toString()
                        .contentEquals(confirmPasswordET.text.toString())
                ) {
                    viewModel.changePassword(
                        token,
                        newPasswordET.text.toString(),
                        confirmPasswordET.text.toString(),
                        ""
                    )
                } else {
                    binding.root.snackBar(getString(R.string.error_password_not_match))
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
                    if (newPasswordET.text.toString() == confirmPasswordET.text.toString()) {
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
                    if (confirmPasswordET.text.toString() == newPasswordET.text.toString()) {
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

    private fun setConfirmETTick() {
        confirmPasswordIL.startIconDrawable =
            AppCompatResources.getDrawable(applicationContext, R.drawable.ic_tick)
        confirmPasswordIL.setStartIconTintList(
            AppCompatResources.getColorStateList(
                applicationContext,
                R.color.colorPrimary
            )
        )
    }

    private fun setConfirmETCross() {
        confirmPasswordIL.startIconDrawable =
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.ic_round_close_24
            )
        confirmPasswordIL.setStartIconTintList(
            AppCompatResources.getColorStateList(
                applicationContext,
                R.color.colorPrimary
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (intent.getBooleanExtra("isFromOTPScreen", false)) {
            oldPasswordIL.visibility = View.GONE
        }
    }

    private fun initViews() {
        newPasswordET = binding.newPasswordET
        oldPasswordET = binding.oldPasswordET
        confirmPasswordET = binding.confirmPasswordET
        oldPasswordIL = binding.oldPasswordIL
        confirmPasswordIL = binding.confirmPasswordIL
    }

    private fun setObserver() {
        viewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.hide()

                    val finishAllActivitiesExceptLogin = Intent(this, LoginActivity::class.java)
                    finishAllActivitiesExceptLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(finishAllActivitiesExceptLogin)
                    // bind data to the view
                }
                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    toast(response.message!!)
                    // show error message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    binding.root.snackBar("Changing password...")
                    // show a progress bar
                }
            }
        }

//        changePasswordViewModel.changePasswordResponse.observe(this, {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    this.toast(it.data?.title!!)
//                    val finishAllActivitiesExceptLogin = Intent(this, LoginActivity::class.java)
//                    finishAllActivitiesExceptLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                    startActivity(finishAllActivitiesExceptLogin)
//                }
//                Status.ERROR -> {
//                    binding.root.snackBar(it.message!!)
//                }
//                Status.LOADING -> {
//                    binding.root.snackBar("Changing password...")
//                }
//            }
//        })
    }

}