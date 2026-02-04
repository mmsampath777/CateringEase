package com.example.caterease

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {

    private lateinit var adapter: MenuAdapter
    private val fullMenu = mutableListOf<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // 1. Fill your list with multiple items
        fullMenu.add(MenuItem(1, "Classic Idli", 120.0, R.drawable.bg_catering, true, "Breakfast"))
        fullMenu.add(MenuItem(2, "Chicken Biryani", 280.0, R.drawable.chicken_biryani, false, "Lunch"))
        fullMenu.add(MenuItem(3, "Masala Dosa", 150.0, R.drawable.masala_dosa, true, "Breakfast"))
        fullMenu.add(MenuItem(4, "Mutton Sukka", 350.0, R.drawable.mutton_chukka, false, "Starters"))
        fullMenu.add(MenuItem(5, "Veg Meals", 180.0, R.drawable.veg_meals, true, "Lunch"))

        val rv = findViewById<RecyclerView>(R.id.rvMenuGrid)
        adapter = MenuAdapter(fullMenu) { item ->
            // Add to Cart logic
            Toast.makeText(this, "${item.name} added to cart!", Toast.LENGTH_SHORT).show()
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
    }
}