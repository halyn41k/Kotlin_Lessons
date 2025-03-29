package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import com.example.newfragment.databinding.FragmentCartBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment(), CartListFragment.OnCartInteractionListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private val currentUserId = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCartContent()
    }

    private fun loadCartContent() {
        CoroutineScope(Dispatchers.IO).launch {
            val cartProducts = db.cartProductDao().getAllCartProducts(currentUserId)
            withContext(Dispatchers.Main) {
                if (cartProducts.isEmpty()) {
                    binding.emptyCartMessage.visibility = View.VISIBLE
                    binding.cartContentContainer.visibility = View.GONE
                } else {
                    binding.emptyCartMessage.visibility = View.GONE
                    binding.cartContentContainer.visibility = View.VISIBLE
                    showCartList(cartProducts)
                }
            }
        }
    }

    private fun showCartList(cartProducts: List<CartProduct>) {
        CoroutineScope(Dispatchers.IO).launch {
            val productIds = cartProducts.map { it.productId }.distinct()
            val products = db.productDao().getProductsByIds(productIds)
            val cartItems = cartProducts.mapNotNull { cartProduct ->
                products.find { product -> product.id == cartProduct.productId }?.let { product ->
                    Pair(cartProduct, product)
                }
            }
            withContext(Dispatchers.Main) {
                val cartItemsArray = ArrayList<Pair<CartProduct, Product>>().apply { addAll(cartItems) }
                val cartListFragment = CartListFragment.newInstance(cartItemsArray)
                childFragmentManager.beginTransaction()
                    .replace(binding.cartContentContainer.id, cartListFragment)
                    .commit()
            }
        }
    }

    override fun onCheckoutClicked(cartItems: ArrayList<Pair<CartProduct, Product>>) {
        // Ручна транзакція для PaymentFragment
        val paymentFragment = PaymentFragment.newInstance(cartItems)
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, paymentFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCartUpdated() {
        loadCartContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
