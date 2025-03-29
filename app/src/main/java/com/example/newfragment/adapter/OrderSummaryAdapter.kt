package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.R
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import java.text.NumberFormat
import java.util.Locale

class OrderSummaryAdapter(private var cartItems: List<Pair<CartProduct, Product>>) :
    RecyclerView.Adapter<OrderSummaryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_summary_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (cartProduct, product) = cartItems[position]

        holder.productNameTextView.text = product.name
        holder.quantityTextView.text = String.format(Locale.getDefault(), "Кількість: %d", cartProduct.quantity)

        val itemTotal = product.price * cartProduct.quantity
        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val formattedItemTotal = format.format(itemTotal)

        holder.priceTextView.text = formattedItemTotal
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newCartItems: List<Pair<CartProduct, Product>>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
    }
}