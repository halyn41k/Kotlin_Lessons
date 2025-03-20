package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.CartListFragment
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import com.example.newfragment.databinding.CartItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private var cartItems: List<Pair<CartProduct, Product>>,
    private val listener: CartListFragment.OnCartInteractionListener?
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (cartProduct, product) = cartItems[position]

        // Прив'язуємо назву продукту до TextView
        holder.binding.cartItemName.text = product.name

        // Форматуємо ціну згідно з локаллю України та встановлюємо її
        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        holder.binding.cartItemPrice.text = format.format(product.price)

        // Встановлюємо кількість продукту
        holder.binding.cartItemQuantity.text = cartProduct.quantity.toString()

        // Обробка кліку на кнопку збільшення кількості
        holder.binding.incrementButton.setOnClickListener {
            // Реалізуйте логіку збільшення кількості товару
        }

        // Обробка кліку на кнопку зменшення кількості
        holder.binding.decrementButton.setOnClickListener {
            // Реалізуйте логіку зменшення кількості товару
        }

        // Обробка кліку на кнопку видалення товару з кошика
        holder.binding.removeCartItemButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(holder.itemView.context)
                    .cartProductDao()
                    .delete(cartProduct)
                withContext(Dispatchers.Main) {
                    listener?.onCartUpdated()
                }
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateData(newCartItems: List<Pair<CartProduct, Product>>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }

    fun getCartItems(): List<Pair<CartProduct, Product>> = cartItems
}
