package com.bagus.finbud.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bagus.finbud.R
import com.bagus.finbud.preferences.PreferenceManager

class SplashActivity : BaseActivity() {
    private val pref by lazy { PreferenceManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed( {
//          startActivity(Intent(this, HomeActivity::class.java))
            if (pref.getInt("pref_is_login") == 0) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 3000)
    }
}