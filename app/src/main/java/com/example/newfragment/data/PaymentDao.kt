package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: Payment)

    @Query("SELECT * FROM payments WHERE id = :paymentId")
    suspend fun getPaymentById(paymentId: Int): Payment?


    @Query("SELECT * FROM payments WHERE userId = :userId")
    suspend fun getPaymentsByUser(userId: Int): List<Payment>

    // Можете додати інші необхідні запити
}