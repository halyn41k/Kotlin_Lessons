package com.example.newfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newfragment.adapter.CartAdapter
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import com.example.newfragment.databinding.FragmentCartListBinding
import java.text.NumberFormat
import java.util.Locale

class CartListFragment : Fragment() {

    private var _binding: FragmentCartListBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter
    private var cartItems: ArrayList<Pair<CartProduct, Product>> = arrayListOf()

    private var listener: OnCartInteractionListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnCartInteractionListener) {
            listener = parentFragment as OnCartInteractionListener
        } else {
            throw RuntimeException("$context must implement OnCartInteractionListener")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cartItems =
                it.getSerializable(ARG_CART_ITEMS) as ArrayList<Pair<CartProduct, Product>>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartAdapter = CartAdapter(cartItems, listener) // Pass listener to adapter
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = cartAdapter

        calculateAndDisplayTotalPrice()

        binding.checkoutButton.setOnClickListener {
            listener?.onCheckoutClicked(cartItems)
        }
    }

    private fun calculateAndDisplayTotalPrice() {
        var totalPrice = 0.0
        for ((cartProduct, product) in cartItems) {
            totalPrice += product.price * cartProduct.quantity
        }

        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val formattedTotalPrice = format.format(totalPrice)
        binding.totalPriceTextView.text = "Загалом: $formattedTotalPrice"
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CART_ITEMS = "cartItems"

        @JvmStatic
        fun newInstance(cartItems: ArrayList<Pair<CartProduct, Product>>) =
            CartListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CART_ITEMS, cartItems)
                }
            }
    }

    interface OnCartInteractionListener {
        fun onCheckoutClicked(cartItems: ArrayList<Pair<CartProduct, Product>>)
        fun onCartUpdated() // Додаємо метод для оновлення кошика
    }
}