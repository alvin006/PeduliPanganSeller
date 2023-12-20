package com.alvintio.pedulipanganseller.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.alvintio.pedulipanganseller.MainActivity
import com.alvintio.pedulipanganseller.R
import com.alvintio.pedulipanganseller.databinding.ActivityAuthenticationBinding
import com.alvintio.pedulipanganseller.utils.Helper

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, LoginFragment.newInstance())
                .commit()
        }
        Helper.setupFullScreen(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}