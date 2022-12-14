package com.clxns.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.clxns.app.data.preference.SessionManager
import com.clxns.app.databinding.ActivitySplashScreenBinding
import com.clxns.app.ui.login.LoginActivity
import com.clxns.app.ui.main.MainActivity
import com.clxns.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding : ActivitySplashScreenBinding

    @Inject
    lateinit var sessionManager : SessionManager //Property will be initialize by Preference Module using Hilt - Dependency Injection

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Checking if the user has already logged in the past, using shared preference and open new activity accordingly
        val isLoggedIn = sessionManager.getBoolean(Constants.IS_USER_LOGGED_IN)
        if (isLoggedIn) {
            openNewActivity(MainActivity())
        } else {
            openNewActivity(LoginActivity())
        }
    }

    /**
     * @param activity Passing New Activity Instance
     */
    private fun openNewActivity(activity : AppCompatActivity) {

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, activity::class.java)
            startActivity(intent)
            finish()

        }, Constants.SPLASH_SCREEN_TIMEOUT) //Delay of 1 Sec before starting the new activity
    }
}