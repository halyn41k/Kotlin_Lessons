package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newfragment.adapter.WishlistAdapter
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import com.example.newfragment.data.WishlistProduct
import com.example.newfragment.databinding.FragmentWishlistBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!

    private lateinit var wishlistAdapter: WishlistAdapter
    private lateinit var db: AppDatabase
    private val currentUserId = 1 // Replace with actual user ID

    // Interface для зв'язку з CatalogFragment
    interface OnCatalogUpdateListener {
        fun onCatalogUpdated()
    }

    private var catalogUpdateListener: OnCatalogUpdateListener? = null

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        // Перевіряємо, чи реалізує батьківський фрагмент інтерфейс
        if (parentFragment is OnCatalogUpdateListener) {
            catalogUpdateListener = parentFragment as OnCatalogUpdateListener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wishlistAdapter = WishlistAdapter(
            wishlistItems = emptyList(),
            onRemoveClicked = { wishlistProduct ->
                removeFromWishlist(wishlistProduct)
                // Викликаємо метод інтерфейсу для оновлення каталогу
                catalogUpdateListener?.onCatalogUpdated()
            },
            onMoveToCartClicked = { wishlistProduct ->
                moveToCart(wishlistProduct)
            }
        )
        binding.wishlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.wishlistRecyclerView.adapter = wishlistAdapter

        loadWishlistItems()
    }

    private fun showEmptyWishlistMessage(show: Boolean) {
        view?.post {
            if (show) {
                binding.emptyWishlistTextView.visibility = View.VISIBLE
                binding.wishlistRecyclerView.visibility = View.GONE
            } else {
                binding.emptyWishlistTextView.visibility = View.GONE
                binding.wishlistRecyclerView.visibility = View.VISIBLE
            }
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
                wishlistAdapter.updateData(productList)
                showEmptyWishlistMessage(productList.isEmpty())
            }
        }
    }

    private fun removeFromWishlist(wishlistProduct: WishlistProduct) {
        CoroutineScope(Dispatchers.IO).launch {
            db.wishlistProductDao().delete(wishlistProduct)
            withContext(Dispatchers.Main) {
                loadWishlistItems()
                // Викликаємо метод інтерфейсу для оновлення каталогу
                catalogUpdateListener?.onCatalogUpdated()
            }
        }
    }

    private fun moveToCart(wishlistProduct: WishlistProduct) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingCartProduct =
                db.cartProductDao().getCartProduct(currentUserId, wishlistProduct.productId)

            if (existingCartProduct != null) {
                val updatedProduct = existingCartProduct.copy(quantity = existingCartProduct.quantity + 1)
                db.cartProductDao().update(updatedProduct)
            } else {
                val newCartProduct =
                    CartProduct(userId = currentUserId, productId = wishlistProduct.productId, quantity = 1)
                db.cartProductDao().insert(newCartProduct)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Added to Cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        catalogUpdateListener = null
    }
}