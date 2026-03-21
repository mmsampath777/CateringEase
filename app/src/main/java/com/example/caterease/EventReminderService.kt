package com.example.caterease

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class EventReminderService : Service() {

    private val CHANNEL_ID = "event_reminder_channel"
    private val NOTIFICATION_ID = 102

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Static event data
        val eventName = "Wedding Catering"
        val eventDate = "Tomorrow"
        val guests = 150

        val notificationContent = "Your $eventName event is scheduled $eventDate for $guests guests."
        
        val notification = createNotification(notificationContent)
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, notification)
        
        // Return START_STICKY so the service persists
        return START_STICKY
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CaterEase Event Reminder")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification_cart)
            .setOngoing(true) // Makes it persistent
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Event Reminder Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
