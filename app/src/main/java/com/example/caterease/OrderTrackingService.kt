package com.example.caterease

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class OrderTrackingService : Service() {

    private val CHANNEL_ID = "order_tracking_channel"
    private val NOTIFICATION_ID = 101
    private val handler = Handler(Looper.getMainLooper())
    
    private val statuses = arrayOf(
        "Preparing Food",
        "Packing Order",
        "Out for Delivery",
        "Delivered"
    )
    private var currentStatusIndex = 0

    private val statusUpdater = object : Runnable {
        override fun run() {
            if (currentStatusIndex < statuses.size) {
                updateNotification(statuses[currentStatusIndex])
                currentStatusIndex++
                // Update every 5 seconds
                handler.postDelayed(this, 5000)
            } else {
                // Stop service when delivered
                stopForeground(STOP_FOREGROUND_DETACH)
                stopSelf()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("Order Confirmed: Starting tracking...")
        startForeground(NOTIFICATION_ID, notification)
        
        handler.postDelayed(statusUpdater, 2000)
        
        return START_STICKY
    }

    private fun createNotification(status: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CaterEase Order Tracking")
            .setContentText("Status: $status")
            .setSmallIcon(R.drawable.ic_notification_cart) // Ensure this exists
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(status: String) {
        val notification = createNotification(status)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Order Tracking Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        handler.removeCallbacks(statusUpdater)
        super.onDestroy()
    }
}
