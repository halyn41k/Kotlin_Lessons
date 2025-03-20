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

class PaymentAdapter(private var payments: List<Payment>) :
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderIdTextView: TextView = itemView.findViewById(R.id.orderIdTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val paymentMethodTextView: TextView = itemView.findViewById(R.id.paymentMethodTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        holder.orderIdTextView.text = "Замовлення №${payment.orderId}"
        val formattedAmount = NumberFormat.getCurrencyInstance(Locale("uk", "UA")).format(payment.amount)
        holder.amountTextView.text = formattedAmount
        holder.paymentMethodTextView.text = payment.paymentMethod
        holder.statusTextView.text = payment.status
    }

    override fun getItemCount(): Int = payments.size

    fun updateData(newPayments: List<Payment>) {
        payments = newPayments
        notifyDataSetChanged()
    }
}
