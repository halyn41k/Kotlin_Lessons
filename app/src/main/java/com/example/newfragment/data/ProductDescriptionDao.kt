package com.example.newfragment.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ProductDescriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Якщо є конфлікт (наприклад, той самий productId), замінити
    fun insert(description: ProductDescription)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg descriptions: ProductDescription) // Для вставки декількох описів

    @Query("SELECT * FROM product_descriptions WHERE productId = :productId")
    fun getDescriptionForProduct(productId: Int): List<ProductDescription>

    @Query("SELECT * FROM product_descriptions")
    fun getAllProductDescription(): List<ProductDescription>
}