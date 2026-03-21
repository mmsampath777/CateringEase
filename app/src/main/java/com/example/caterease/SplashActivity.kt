package com.example.caterease

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Automatically start the Event Reminder Service when the app opens
        startEventReminderService()

        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)

        btnGetStarted.setOnClickListener {
            // Move to MenuActivity (or MainActivity if you want to see the service controls)
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startEventReminderService() {
        val intent = Intent(this, EventReminderService::class.java)
        
        // Check for notification permission on Android 13+ before starting the service
        // Note: In a real app, you should request this permission if not granted.
        // For this demo, we attempt to start it.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
