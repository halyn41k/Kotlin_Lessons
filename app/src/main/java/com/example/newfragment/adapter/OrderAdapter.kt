package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.R
import com.example.newfragment.data.OrderProduct
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter(private val listener: OnOrderActionListener) :
    ListAdapter<OrderProduct, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    interface OnOrderActionListener {
        fun onConfirmOrder(order: OrderProduct)
        fun onCancelOrder(order: OrderProduct)
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTextView: TextView = itemView.findViewById(R.id.orderIdTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val paymentMethodTextView: TextView = itemView.findViewById(R.id.paymentMethodTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val confirmButton: Button = itemView.findViewById(R.id.confirmButton)
        val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.orderIdTextView.text = "Замовлення №${order.orderId}"

        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val formattedAmount = format.format(order.price * order.quantity)
        holder.amountTextView.text = "Сума: $formattedAmount"

        holder.paymentMethodTextView.text = "Оплата: ${order.paymentMethod}"
        holder.statusTextView.text = "Статус: ${order.status}"

        // Викликаємо методи інтерфейсу при кліку
        holder.confirmButton.setOnClickListener { listener.onConfirmOrder(order) }
        holder.cancelButton.setOnClickListener { listener.onCancelOrder(order) }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<OrderProduct>() {
        override fun areItemsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem == newItem
        }
    }
}
