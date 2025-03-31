package com.example.newfragment.data

import android.adservices.adid.AdId
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_product")
data class OrderProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val quantity: Int,
    val userId: Int,
    val paymentId: Int,
    val price: Double,
    val status: String,         // Нове поле
    val createdAt: Long,         // Нове поле
    val paymentMethod: String,
)
