package com.example.caterease

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(
    private var itemList: List<MenuItem>,
    private val onAddClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val img: ImageView = view.findViewById(R.id.ivFood)
        val type: ImageView = view.findViewById(R.id.ivType)
        val btnAdd: Button = view.findViewById(R.id.btnAddCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = itemList[position]
        holder.name.text = item.name
        holder.price.text = "₹${item.price}"
        holder.img.setImageResource(item.image)

        holder.type.setImageResource(if(item.isVeg) R.drawable.ic_veg else R.drawable.ic_nonveg)

        if (CartManager.isItemInCart(item)) {
            holder.btnAdd.text = "Added"
            holder.btnAdd.isEnabled = false
        } else {
            holder.btnAdd.text = "Add"
            holder.btnAdd.isEnabled = true
            holder.btnAdd.setOnClickListener { 
                onAddClick(item)
            }
        }
    }

    override fun getItemCount() = itemList.size

    fun updateList(newList: List<MenuItem>) {
        itemList = newList
        notifyDataSetChanged()
    }
}