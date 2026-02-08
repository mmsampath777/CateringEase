package com.example.caterease

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {

    private lateinit var adapter: MenuAdapter
    private val fullMenu = mutableListOf<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Apply insets to handle edge-to-edge
        val mainLayout = findViewById<LinearLayout>(R.id.main)
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

        // 1. Fill your list with multiple items
        fullMenu.add(MenuItem(1, "Classic Idli", 120.0, R.drawable.bg_catering, true, "Breakfast"))
        fullMenu.add(MenuItem(2, "Chicken Biryani", 280.0, R.drawable.chicken_biryani, false, "Lunch"))
        fullMenu.add(MenuItem(3, "Masala Dosa", 150.0, R.drawable.masala_dosa, true, "Breakfast"))
        fullMenu.add(MenuItem(4, "Mutton Sukka", 350.0, R.drawable.mutton_chukka, false, "Starters"))
        fullMenu.add(MenuItem(5, "Veg Meals", 180.0, R.drawable.veg_meals, true, "Lunch"))
        fullMenu.add(MenuItem(6, "Gulab Jamun", 90.0, R.drawable.gulab, true, "Sweet"))

        val rv = findViewById<RecyclerView>(R.id.rvMenuGrid)
        adapter = MenuAdapter(fullMenu) { item ->
            // Add to Cart logic
            CartManager.addItem(item)
            Toast.makeText(this, "${item.name} added to cart!", Toast.LENGTH_SHORT).show()
            // Refresh the specific item
            val index = fullMenu.indexOf(item)
            if (index != -1) {
                adapter.notifyItemChanged(index)
            }
        }
        rv.adapter = adapter

        // 2. Search filtering logic
        val searchBar = findViewById<EditText>(R.id.searchBar)
        searchBar.addTextChangedListener { text ->
            val filtered = fullMenu.filter {
                it.name.contains(text.toString(), ignoreCase = true)
            }
            adapter.updateList(filtered)
        }

        // 3. Cart icon click listener
        val cartIcon = findViewById<ImageButton>(R.id.btnViewCart)
        cartIcon.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }
}