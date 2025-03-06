package com.example.newfragment.data

import androidx.room.*

@Dao
interface ProductDao {
    // Додавання продукту
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Додавання кількох продуктів (для додавання списку товарів)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg products: Product)

    // Отримання всіх продуктів
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    // Додавання опису продукту
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductDescription(description: ProductDescription)

    // Отримання опису продукту за ID
    @Query("SELECT * FROM product_descriptions WHERE productId = :productId LIMIT 1")
    suspend fun getProductDescription(productId: Int): ProductDescription?
}
