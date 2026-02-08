package com.example.caterease

data class MenuItem(
    val id: Int,
    val name: String,
    val price: Double,
    val image: Int, // Drawable resource ID
    val isVeg: Boolean,
    val category: String
)
