package com.clxns.app.ui.changePassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.clxns.app.R
import com.clxns.app.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}