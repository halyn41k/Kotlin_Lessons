// ViewModel/WishlistViewModel.kt
package com.example.newfragment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.WishlistProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WishlistViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val wishlistDao = db.wishlistProductDao()

    fun getWishlistForUser(userId: Int): LiveData<List<WishlistProduct>> {
        return wishlistDao.getWishlistForUser(userId)
    }

    fun addToWishlist(wishlistProduct: WishlistProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            wishlistDao.insert(wishlistProduct)
        }
    }

    fun removeFromWishlist(wishlistProduct: WishlistProduct) {
        viewModelScope.launch(Dispatchers.IO) {
            wishlistDao.delete(wishlistProduct)
        }
    }
}