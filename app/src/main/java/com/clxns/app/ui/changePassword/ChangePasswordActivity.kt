package com.clxns.app.ui.changePassword

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.clxns.app.R
import com.clxns.app.databinding.ActivityChangePasswordBinding
import com.clxns.app.utils.Status
import com.clxns.app.utils.removeFocus
import com.clxns.app.utils.toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    private lateinit var newPasswordET: TextInputEditText
    private lateinit var confirmPasswordET: TextInputEditText
    private lateinit var oldPasswordET: TextInputEditText
    private lateinit var oldPasswordIL: TextInputLayout
    private lateinit var confirmPasswordIL: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        setObserver()

        setListeners()

    }

    private fun setListeners() {
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
        if (intent.getBooleanExtra("isFromOTPScreen", false)){
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
        changePasswordViewModel.changePasswordResponse.observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    this.toast(it.data?.title!!)
                }
                Status.ERROR -> {
                    this.toast(it.message!!)
                }
                Status.LOADING -> {
                    this.toast("Loading....")
                }
            }
        })
    }

}