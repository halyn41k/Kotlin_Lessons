package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import com.example.newfragment.data.WishlistProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.res.ResourcesCompat // Import for font
//import com.google.android.material.appbar.MaterialToolbar // REMOVE THIS IMPORT - No longer needed

class WishlistFragment : Fragment() {

    private lateinit var wishlistRecyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var db: AppDatabase
    private lateinit var emptyWishlistTextView: TextView
    private val currentUserId = 1 //  Replace with actual user ID
    private lateinit var wishlistTitle: TextView // Use the TextView for the title


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wishlist, container, false)

        wishlistRecyclerView = view.findViewById(R.id.wishlistRecyclerView)
        wishlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        emptyWishlistTextView = view.findViewById(R.id.emptyWishlistTextView) // Get reference
        db = AppDatabase.getInstance(requireContext())
        wishlistTitle = view.findViewById(R.id.wishlistTitle) // Find the TextView


        wishlistAdapter = WishlistAdapter(
            wishlistItems = emptyList(),
            onRemoveClicked = { wishlistProduct ->
                removeFromWishlist(wishlistProduct)
            },
            onMoveToCartClicked = { wishlistProduct ->
                moveToCart(wishlistProduct)
            }
        )
        wishlistRecyclerView.adapter = wishlistAdapter
        //toolbar.setTitleTextAppearance(requireContext(), R.style.ToolbarTitleStyle) // REMOVE THIS LINE
        loadWishlistItems()


        return view
    }

    private fun showEmptyWishlistMessage(show: Boolean) {
        if (show) {
            emptyWishlistTextView.visibility = View.VISIBLE
            wishlistRecyclerView.visibility = View.GONE // Hide RecyclerView
            emptyWishlistTextView.text = "Нічого не додано" // Correct empty message
        } else {
            emptyWishlistTextView.visibility = View.GONE
            wishlistRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun loadWishlistItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val wishlistProducts = db.wishlistProductDao().getAllWishlistProducts(currentUserId)
            val productList = mutableListOf<Pair<WishlistProduct, Product>>()

            for (wishlistProduct in wishlistProducts) {
                val product = db.productDao().getProductById(wishlistProduct.productId)
                product?.let {
                    productList.add(Pair(wishlistProduct, it))
                }
            }

            withContext(Dispatchers.Main) {
                if (productList.isEmpty()) {
                    showEmptyWishlistMessage(true)
                    wishlistAdapter.updateData(emptyList()) // Update adapter
                } else {
                    showEmptyWishlistMessage(false)
                    wishlistAdapter.updateData(productList)
                }
            }
        }
    }


    private fun removeFromWishlist(wishlistProduct: WishlistProduct) {
        CoroutineScope(Dispatchers.IO).launch {
            db.wishlistProductDao().delete(wishlistProduct)
            loadWishlistItems() // Reload the list
        }
    }

    private fun moveToCart(wishlistProduct: WishlistProduct) {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Add to Cart (check if already exists, update quantity or insert)
            val existingCartProduct = db.cartProductDao().getCartProduct(currentUserId, wishlistProduct.productId)

            if (existingCartProduct != null) {
                val updatedProduct = existingCartProduct.copy(quantity = existingCartProduct.quantity + 1)
                db.cartProductDao().insert(updatedProduct)
            } else {
                val newCartProduct = CartProduct(userId = currentUserId, productId = wishlistProduct.productId, quantity = 1)
                db.cartProductDao().insert(newCartProduct)
            }

            // 2. Remove from Wishlist
            db.wishlistProductDao().delete(wishlistProduct)

            // 3. Reload both lists (Wishlist and potentially Cart)
            loadWishlistItems() // Reload Wishlist here

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Moved to Cart", Toast.LENGTH_SHORT).show()
            }
        }
    }
}