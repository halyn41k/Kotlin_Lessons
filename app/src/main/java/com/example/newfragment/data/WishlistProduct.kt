package com.example.newfragment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist_product")
data class WishlistProduct(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Прив'язка до `User`
    val productId: Int // Прив'язка до `Product`
)
