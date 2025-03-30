package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE id IN (:productIds)")
    fun getProductsByIds(productIds: List<Int>): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg products: Product): LongArray

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductDescription(description: ProductDescription)

    @Query("SELECT * FROM product_descriptions WHERE productId = :productId LIMIT 1")
    suspend fun getProductDescription(productId: Int): ProductDescription?

    @Query("SELECT * FROM products")
    fun getPopularProducts(): List<Product>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getNewProducts(): List<Product>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    suspend fun searchProducts(query: String): List<Product>
}
