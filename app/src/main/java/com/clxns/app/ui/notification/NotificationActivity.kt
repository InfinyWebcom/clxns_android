package com.clxns.app.ui.notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.clxns.app.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNotificationBinding
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setListeners()
    }

    private fun setListeners() {
        binding.notificationBackBtn.setOnClickListener { onBackPressed() }
    }
}