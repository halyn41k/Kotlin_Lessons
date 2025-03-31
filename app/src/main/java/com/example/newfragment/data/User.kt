package com.example.newfragment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lastName: String,
    val firstName: String,
    val role: String = "client", // "admin" або "client"
    val email: String,
    val phoneNumber: String,
    val password: String,
    val access: Boolean = true, // true - активний, false - заблокований
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)