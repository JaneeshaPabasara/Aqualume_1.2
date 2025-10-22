package com.example.aqualumedb

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        val btnGetStarted = findViewById<Button>(R.id.btn_get_started)
        btnGetStarted.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

//        // Auto-navigate after 3 seconds if button not clicked
//        Handler(Looper.getMainLooper()).postDelayed({
//            if (!isFinishing) {
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
//            }
//        }, 3000)
    }
}