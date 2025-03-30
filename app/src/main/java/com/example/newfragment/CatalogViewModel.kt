package com.example.newfragment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import com.example.newfragment.data.WishlistProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val productDao = db.productDao()
    private val cartProductDao = db.cartProductDao()
    private val wishlistProductDao = db.wishlistProductDao()

    // LiveData для списку популярних товарів
    private val _popularProducts = MutableLiveData<List<Product>>()
    val popularProducts: LiveData<List<Product>> get() = _popularProducts

    // LiveData для списку нових товарів
    private val _newProducts = MutableLiveData<List<Product>>()
    val newProducts: LiveData<List<Product>> get() = _newProducts

    init {
        loadPopularProducts()
        loadNewProducts()
    }

    // Завантаження популярних товарів
    private fun loadPopularProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val products = productDao.getPopularProducts()
            withContext(Dispatchers.Main) {
                _popularProducts.value = products
            }
        }
    }

    // Завантаження нових товарів
    private fun loadNewProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            val products = productDao.getNewProducts()
            withContext(Dispatchers.Main) {
                _newProducts.value = products
            }
        }
    }

    // Додавання товару в кошик
    fun addToCart(userId: Int, product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingCartItem = cartProductDao.getProduct(userId, product.id)
            if (existingCartItem != null) {
                val updatedItem = existingCartItem.copy(quantity = existingCartItem.quantity + 1)
                cartProductDao.update(updatedItem)
            } else {
                cartProductDao.insert(CartProduct(userId = userId, productId = product.id, quantity = 1))
            }
        }
    }

    // Зміна списку бажаного (додавання або видалення)
    fun toggleWishlistItem(userId: Int, product: Product) {
        viewModelScope.launch(Dispatchers.IO) {
            val isInWishlist = wishlistProductDao.isInWishlist(userId, product.id)
            if (isInWishlist) {
                wishlistProductDao.deleteByUserIdAndProductId(userId, product.id)
            } else {
                wishlistProductDao.insert(WishlistProduct(userId = userId, productId = product.id))
            }
            // Після зміни бажаного, оновлюємо список продуктів, щоб відобразити зміни
            // Це може бути не дуже ефективно, якщо у вас багато продуктів.
            // Можна використовувати LiveData для оновлення конкретного елемента, якщо це необхідно.
            loadPopularProducts()
            loadNewProducts()
        }
    }
}