package com.example.newfragment.data

import androidx.room.*

@Dao
interface UserDao {
    // Додавання користувача (якщо існує - замінити)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Отримати користувача за email і паролем
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    // Отримати користувача за email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    // Оновити доступ користувача (повертає кількість змінених рядків)
    @Query("UPDATE users SET access = :access WHERE id = :userId")
    suspend fun updateAccess(userId: Int, access: Boolean): Int

    @Delete
    suspend fun deleteUser(user: User): Int


    // Отримати всіх користувачів
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}
