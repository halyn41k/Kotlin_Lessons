package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: Payment): Long

    @Query("SELECT * FROM payments WHERE id = :paymentId")
    suspend fun getPaymentById(paymentId: Int): Payment?

    @Query("SELECT * FROM order_product WHERE userId = :userId")
    suspend fun getOrderProductsByUser(userId: Int): List<OrderProduct>

    // Можете додати інші необхідні запити
}