package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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

}