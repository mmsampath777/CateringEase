package com.example.caterease

data class MenuItem(
    val id: Int,
    val name: String,
    val price: Double,
    val imageRes: Int, // e.g., R.drawable.idli
    val isVeg: Boolean,
    val category: String
)