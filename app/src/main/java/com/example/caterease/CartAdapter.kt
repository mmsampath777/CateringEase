package com.example.caterease

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val items: List<CartItem>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvCartName)
        val price: TextView = v.findViewById(R.id.tvCartPrice)
        val qty: TextView = v.findViewById(R.id.tvQty)
        val img: ImageView = v.findViewById(R.id.ivCartImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.menuItem.name
        holder.price.text = "₹${item.menuItem.price * item.quantity}"
        holder.qty.text = "Qty: ${item.quantity}"
        holder.img.setImageResource(item.menuItem.image)
    }

    override fun getItemCount() = items.size
}