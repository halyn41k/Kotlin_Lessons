package com.example.newfragment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_descriptions")
data class ProductDescription(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int, // Прив'язка до `Product`
    val beadProducer: String,
    val weight: Double,
    val countryOfManufacture: String,
    val typeOfBead: String,
    val category: String
)
