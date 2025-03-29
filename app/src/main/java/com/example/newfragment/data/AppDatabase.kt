package com.example.newfragment.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Product::class, ProductDescription::class, CartProduct::class, WishlistProduct::class, OrderProduct::class, Payment::class],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun productDescriptionDao(): ProductDescriptionDao
    abstract fun cartProductDao(): CartProductDao  // Add this
    abstract fun wishlistProductDao(): WishlistProductDao // Add this
    abstract fun paymentDao(): PaymentDao
    abstract fun orderProductDao(): OrderProductDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}