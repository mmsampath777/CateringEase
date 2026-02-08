package com.example.caterease

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val mainLayout = findViewById<RelativeLayout>(R.id.main)
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

        val rv = findViewById<RecyclerView>(R.id.rvCartItems)
        val total = findViewById<TextView>(R.id.tvCartTotal)
        val proceedButton = findViewById<Button>(R.id.btnProceed)

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = CartAdapter(CartManager.getCartItems())

        total.text = "₹${CartManager.getTotalPrice()}"

        proceedButton.setOnClickListener {
            startActivity(Intent(this, EventDetailsActivity::class.java))
        }
    }
}