package com.example.caterease

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity serves as the entry point to demonstrate Foreground Services and Multimedia features.
 */
class MainActivity : AppCompatActivity() {

    private var introVideoView: VideoView? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Video Feature ---
        introVideoView = findViewById(R.id.introVideoView)
        
        // Path to the video file in res/raw
        val videoPath = "android.resource://" + packageName + "/" + R.raw.intro
        val uri = Uri.parse(videoPath)
        introVideoView?.setVideoURI(uri)

        // Start video automatically
        introVideoView?.start()

        // Loop the video continuously
        introVideoView?.setOnCompletionListener {
            introVideoView?.start()
        }

        // --- Audio Feature ---
        val btnPlaceOrder = findViewById<Button>(R.id.btnPlaceOrderDemo)
        btnPlaceOrder.setOnClickListener {
            playConfirmationSound()
        }

        // --- Foreground Service Features ---
        // Button to start the Order Tracking Service
        val btnStartOrderTracking = findViewById<Button>(R.id.btnStartOrderTracking)
        btnStartOrderTracking.setOnClickListener {
            val intent = Intent(this, OrderTrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

        // Button to stop the Order Tracking Service
        val btnStopOrderTracking = findViewById<Button>(R.id.btnStopOrderTracking)
        btnStopOrderTracking.setOnClickListener {
            stopService(Intent(this, OrderTrackingService::class.java))
        }

        // Button to start the Event Reminder Service
        val btnStartEventReminder = findViewById<Button>(R.id.btnStartEventReminder)
        btnStartEventReminder.setOnClickListener {
            val intent = Intent(this, EventReminderService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }

        // Button to stop the Event Reminder Service
        val btnStopEventReminder = findViewById<Button>(R.id.btnStopEventReminder)
        btnStopEventReminder.setOnClickListener {
            stopService(Intent(this, EventReminderService::class.java))
        }
    }

    private fun playConfirmationSound() {
        // Release previous mediaPlayer if it exists
        mediaPlayer?.release()

        // Initialize MediaPlayer with the local audio file
        mediaPlayer = MediaPlayer.create(this, R.raw.ordconfirm)
        
        // Start playing the sound
        mediaPlayer?.start()

        // Release resources once the sound finishes playing
        mediaPlayer?.setOnCompletionListener { mp ->
            mp.release()
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume video if the app comes back to foreground
        introVideoView?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up MediaPlayer resources
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
