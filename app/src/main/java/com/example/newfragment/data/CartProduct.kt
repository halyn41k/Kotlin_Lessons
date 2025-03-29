package com.example.newfragment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_product")
data class CartProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Прив'язка до `User`
    val productId: Int, // Прив'язка до `Product`
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)