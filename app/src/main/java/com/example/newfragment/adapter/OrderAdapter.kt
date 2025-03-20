package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.R
import com.example.newfragment.data.Payment
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter(private var orders: List<Payment>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderTitleTextView: TextView = itemView.findViewById(R.id.orderTitleTextView)
        val orderIdTextView: TextView = itemView.findViewById(R.id.orderIdTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val paymentMethodTextView: TextView = itemView.findViewById(R.id.paymentMethodTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderTitleTextView.text = "Замовлення"
        holder.orderIdTextView.text = "Номер замовлення: ${order.orderId}"
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale("uk", "UA")).format(order.amount)
        holder.amountTextView.text = "Сума: $formattedAmount"
        holder.paymentMethodTextView.text = "Спосіб оплати: ${order.paymentMethod}"
        holder.statusTextView.text = "Статус: ${order.status}"
    }

    override fun getItemCount(): Int = orders.size

    fun updateData(newOrders: List<Payment>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}
