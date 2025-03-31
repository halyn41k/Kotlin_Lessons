package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderProduct: OrderProduct): Long

    @Query("SELECT * FROM order_product")
    suspend fun getAllOrderProducts(): List<OrderProduct>

    @Query("SELECT * FROM order_product WHERE orderId = :orderId")
    suspend fun getOrderProductsByOrderId(orderId: Int): List<OrderProduct>

    // Новий метод для оновлення статусу замовлення
    @Query("UPDATE order_product SET status = :newStatus WHERE id = :id")
    suspend fun updateOrderStatus(id: Int, newStatus: String)
}
