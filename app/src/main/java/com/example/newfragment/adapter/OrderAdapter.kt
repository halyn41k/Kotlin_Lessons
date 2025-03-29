package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.R
import com.example.newfragment.data.OrderProduct
import java.text.NumberFormat
import java.util.Locale

// Виправляємо ListAdapter для роботи з OrderProduct
class OrderAdapter(private val listener: OnOrderActionListener) :
    ListAdapter<OrderProduct, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    interface OnOrderActionListener {
        fun onConfirmOrder(orderId: Int)
        fun onCancelOrder(orderId: Int)
    }

    // Виправляємо OrderViewHolder
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Переконайтеся, що ці ідентифікатори відповідають елементам у вашому item_order.xml
        val orderIdTextView: TextView = itemView.findViewById(R.id.orderIdTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val paymentMethodTextView: TextView = itemView.findViewById(R.id.paymentMethodTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    // Виправляємо OrderDiffCallback
    class OrderDiffCallback : DiffUtil.ItemCallback<OrderProduct>() {
        override fun areItemsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem == newItem
        }
    }

    // Додаємо обов'язковий метод onBindViewHolder
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.orderIdTextView.text = "Замовлення №${order.orderId}"

        // Форматування суми
        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val formattedAmount = format.format(order.price * order.quantity) // Обчислюємо суму

        holder.amountTextView.text = "Сума: $formattedAmount"
        holder.paymentMethodTextView.text = "Оплата: ${order.paymentMethod}"
        holder.statusTextView.text = "Статус: ${order.status}"
    }
}