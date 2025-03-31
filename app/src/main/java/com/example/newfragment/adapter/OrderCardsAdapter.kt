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

class OrderCardsAdapter(private val listener: OnStatusChangeListener) :
    ListAdapter<OrderProduct, OrderCardsAdapter.OrderCardViewHolder>(OrderDiffCallback()) {

    interface OnStatusChangeListener {
        fun onChangeStatusClicked(order: OrderProduct)
    }

    class OrderCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTextView: TextView = itemView.findViewById(R.id.orderIdTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val paymentMethodTextView: TextView = itemView.findViewById(R.id.paymentMethodTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
        val changeStatusButton: Button = itemView.findViewById(R.id.changeStatusButton)
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<OrderProduct>() {
        override fun areItemsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderProduct, newItem: OrderProduct): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_card, parent, false)
        return OrderCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderCardViewHolder, position: Int) {
        val order = getItem(position)
        holder.orderIdTextView.text = "Замовлення №${order.orderId}"
        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val formattedAmount = format.format(order.price * order.quantity)
        holder.amountTextView.text = "Сума: $formattedAmount"
        holder.paymentMethodTextView.text = "Оплата: ${order.paymentMethod}"
        holder.statusTextView.text = "Статус: ${order.status}"

        holder.changeStatusButton.setOnClickListener {
            listener.onChangeStatusClicked(order)
        }
    }
}
