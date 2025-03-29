package com.example.newfragment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Прив'язка до `User`
    val orderId: Int, // Прив'язка до замовлення
    val amount: Double,
    val paymentMethod: String, // Наприклад: "card", "cash"
    val status: String, // Наприклад: "pending", "completed", "failed"
    val createdAt: Long = System.currentTimeMillis(),
)