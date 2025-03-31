package com.example.newfragment.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "product_descriptions",
    foreignKeys = [ForeignKey(
        entity = Product::class,
        parentColumns = ["id"],
        childColumns = ["productId"],
        onDelete = ForeignKey.CASCADE // Каскадне видалення
    )],
    indices = [Index("productId")] // Індекс для прискорення запитів
)
data class ProductDescription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val beadProducer: String,
    val weight: Double,
    val countryOfManufacture: String,
    val typeOfBead: String,
    val category: String,
    val accessories: String, // Фурнітура
    val size: String        // Розмір
)