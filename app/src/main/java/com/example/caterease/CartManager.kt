package com.example.caterease

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addItem(item: MenuItem) {
        val existingItem = cartItems.find { it.menuItem.id == item.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(menuItem = item))
        }
    }

    fun getCartItems(): List<CartItem> {
        return cartItems
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.menuItem.price * it.quantity }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun isItemInCart(item: MenuItem): Boolean {
        return cartItems.any { it.menuItem.id == item.id }
    }
}
