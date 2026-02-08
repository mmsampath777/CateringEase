package com.example.caterease

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class OrderSummaryActivity : AppCompatActivity() {

    private val CHANNEL_ID = "order_confirmation_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)

        val mainLayout = findViewById<ScrollView>(R.id.main)
        val initialPaddingTop = mainLayout.paddingTop
        val initialPaddingBottom = mainLayout.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                initialPaddingTop + systemBars.top,
                view.paddingRight,
                initialPaddingBottom + systemBars.bottom
            )
            insets
        }

        val eventDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EVENT_DETAILS", EventDetails::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EVENT_DETAILS") as? EventDetails
        }

        val tvEventDetails = findViewById<TextView>(R.id.tvEventDetails)
        val rvSummaryItems = findViewById<RecyclerView>(R.id.rvSummaryItems)
        val tvSubtotal = findViewById<TextView>(R.id.tvSubtotal)
        val tvGst = findViewById<TextView>(R.id.tvGst)
        val tvDelivery = findViewById<TextView>(R.id.tvDelivery)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        val confirmOrderButton = findViewById<Button>(R.id.btnConfirmOrder)

        if (eventDetails != null) {
            tvEventDetails.text = "Date: ${eventDetails.eventDate}\n" +
                    "Type: ${eventDetails.eventType}\n" +
                    "People: ${eventDetails.numPeople}\n" +
                    "Address: ${eventDetails.address}\n" +
                    "Phone: ${eventDetails.phoneNumber}"
        }

        rvSummaryItems.layoutManager = LinearLayoutManager(this)
        rvSummaryItems.adapter = CartAdapter(CartManager.getCartItems())

        val subtotal = CartManager.getTotalPrice() * (eventDetails?.numPeople?.toIntOrNull() ?: 1)
        val gst = subtotal * 0.18
        val delivery = 50.0 // Example delivery charge
        val total = subtotal + gst + delivery

        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        tvSubtotal.text = format.format(subtotal)
        tvGst.text = format.format(gst)
        tvDelivery.text = format.format(delivery)
        tvTotal.text = format.format(total)

        confirmOrderButton.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Order")
            .setMessage("Are you sure you want to place this order?")
            .setPositiveButton("Confirm") { _, _ ->
                placeOrder()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun placeOrder() {
        createNotificationChannel()
        showNotification()
        CartManager.clearCart()

        val intent = Intent(this, MenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Order Confirmation"
            val descriptionText = "Notifications for order confirmations"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_cart)
            .setContentTitle("Order Confirmed!")
            .setContentText("Your order has been confirmed.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // In a production app, you should check for the POST_NOTIFICATIONS permission before calling notify().
            notify(1, builder.build())
        }
    }
}
