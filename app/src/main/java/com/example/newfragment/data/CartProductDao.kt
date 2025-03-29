package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update

@Dao
interface CartProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Or IGNORE, depending on your needs
    suspend fun insert(cartProduct: CartProduct)

    @Query("SELECT * FROM cart_product WHERE userId = :userId AND productId = :productId")
    suspend fun getCartProduct(userId: Int, productId: Int): CartProduct?

    @Delete
    suspend fun delete(cartProduct: CartProduct)

    @Query("SELECT * FROM cart_product WHERE userId = :userId")
    suspend fun getAllCartProducts(userId: Int): List<CartProduct>

    @Update
    suspend fun update(cartProduct: CartProduct)

    @Query("SELECT * FROM cart_product WHERE userId = :userId AND productId = :productId LIMIT 1")
    suspend fun getProduct(userId: Int, productId: Int): CartProduct?

    @Query("SELECT SUM(quantity) FROM cart_product WHERE userId = :userId")
    suspend fun getCartCount(userId: Int): Int?  // Changed to nullable Int?
}