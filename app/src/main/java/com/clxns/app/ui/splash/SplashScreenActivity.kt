package com.clxns.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivitySplashScreenBinding
import com.clxns.app.ui.MainActivity
import com.clxns.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashScreenBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLoggedIn = sessionManager.getBoolean(Constants.IS_USER_LOGGED_IN)
        if (isLoggedIn) {
            openNewActivity(MainActivity())
        } else {
            openNewActivity(MainActivity())
        }
    }

    private fun openNewActivity(activity: AppCompatActivity) {

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()

        }, Constants.SPLASH_SCREEN_TIMEOUT)
    }
}