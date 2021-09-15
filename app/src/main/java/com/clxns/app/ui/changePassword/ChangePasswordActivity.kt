package com.clxns.app.ui.changePassword

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.clxns.app.databinding.ActivityChangePasswordBinding
import com.clxns.app.utils.Status
import com.clxns.app.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val changePasswordViewModel : ChangePasswordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObserver()

    }

    private fun setObserver() {
        changePasswordViewModel.changePasswordResponse.observe(this,{
            when(it.status){
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