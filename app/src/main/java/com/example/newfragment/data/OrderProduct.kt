package com.example.newfragment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_product")
data class OrderProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: Int, // Ідентифікатор замовлення
    val productId: Int, // Прив'язка до `Product`
    val quantity: Int,
    val price: Double
)