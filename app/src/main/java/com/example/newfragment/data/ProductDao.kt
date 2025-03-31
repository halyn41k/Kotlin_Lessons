package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {

    // Отримання товарів за списком id
    @Query("SELECT * FROM products WHERE id IN (:productIds)")
    suspend fun getProductsByIds(productIds: List<Int>): List<Product>

    // Додавання або оновлення товару
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Додавання або оновлення масиву товарів
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg products: Product): LongArray

    // Отримання всіх товарів
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    // Отримання товару за id (один запис)
    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): Product?

    // Додавання або оновлення опису товару
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductDescription(description: ProductDescription)

    // Отримання опису товару за productId
    @Query("SELECT * FROM product_descriptions WHERE productId = :productId LIMIT 1")
    suspend fun getProductDescription(productId: Int): ProductDescription?

    // Отримання популярних товарів (всі товари)
    @Query("SELECT * FROM products")
    suspend fun getPopularProducts(): List<Product>

    // Отримання нових товарів, відсортованих за датою створення (від новіших до старіших)
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    suspend fun getNewProducts(): List<Product>

    // Видалення товару з бази даних
    @Delete
    suspend fun deleteProduct(product: Product)
}
