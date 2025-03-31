package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.lifecycle.LiveData // Переконайтеся, що цей імпорт є

@Dao
interface WishlistProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wishlistProduct: WishlistProduct)

    @Query("SELECT * FROM wishlist_product WHERE userId = :userId AND productId = :productId")
    suspend fun getWishlistProduct(userId: Int, productId: Int): WishlistProduct?

    @Delete
    suspend fun delete(wishlistProduct: WishlistProduct)

    @Query("SELECT * FROM wishlist_product WHERE userId = :userId")
    suspend fun getAllWishlistProducts(userId: Int): List<WishlistProduct>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_product WHERE userId = :userId AND productId = :productId)")
    suspend fun isInWishlist(userId: Int, productId: Int): Boolean

    @Query("SELECT * FROM wishlist_product WHERE userId = :userId")
    fun getWishlistForUser(userId: Int): LiveData<List<WishlistProduct>>
}