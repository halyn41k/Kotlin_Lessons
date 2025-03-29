package com.example.newfragment.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.newfragment.CartListFragment;
import com.example.newfragment.R;
import com.example.newfragment.data.AppDatabase;
import com.example.newfragment.data.CartProduct;
import com.example.newfragment.data.Product;
import com.example.newfragment.databinding.CartItemBinding;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;
import kotlinx.coroutines.withContext;
import java.text.NumberFormat;
import java.util.Locale;

class CartAdapter(
    private var cartItems: List<Pair<CartProduct, Product>>,
    private val listener: CartListFragment.OnCartInteractionListener?
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (cartProduct, product) = cartItems[position];

        Glide.with(holder.itemView.context)
            .load(product.imageResId)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
            .into(holder.binding.cartItemImage);

        holder.binding.cartItemName.text = product.name;

        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"));
        holder.binding.cartItemPrice.text = format.format(product.price);

        holder.binding.cartItemQuantity.text = cartProduct.quantity.toString();

        // Обробка кліку на кнопку збільшення кількості
        holder.binding.incrementButton.setOnClickListener {
            // 1. Створюємо КОПІЮ з оновленою кількістю
            val updatedCartProduct = cartProduct.copy(quantity = cartProduct.quantity + 1)

            // 2. Оновлюємо UI
            holder.binding.cartItemQuantity.text = updatedCartProduct.quantity.toString()
            notifyItemChanged(position)

            // 3. Асинхронно оновлюємо БД
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(holder.itemView.context).cartProductDao().update(updatedCartProduct)
                withContext(Dispatchers.Main) {
                    listener?.onCartUpdated()
                }
            }
        }

        // Обробка кліку на кнопку зменшення кількості
        holder.binding.decrementButton.setOnClickListener {
            if (cartProduct.quantity > 1) {
                // 1. Створюємо КОПІЮ з оновленою кількістю
                val updatedCartProduct = cartProduct.copy(quantity = cartProduct.quantity - 1)

                // 2. Оновлюємо UI
                holder.binding.cartItemQuantity.text = updatedCartProduct.quantity.toString()
                notifyItemChanged(position)

                // 3. Асинхронно оновлюємо БД
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getInstance(holder.itemView.context).cartProductDao().update(updatedCartProduct)
                    withContext(Dispatchers.Main) {
                        listener?.onCartUpdated()
                    }
                }
            } else {
                // Видаляємо товар
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getInstance(holder.itemView.context).cartProductDao().delete(cartProduct)
                    withContext(Dispatchers.Main) {
                        cartItems = cartItems.filterIndexed { index, _ -> index != position }
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, cartItems.size)
                        listener?.onCartUpdated()
                    }
                }
            }
        }
        // Обробка кліку на кнопку видалення (без змін)
        holder.binding.removeCartItemButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(holder.itemView.context)
                    .cartProductDao()
                    .delete(cartProduct)
                withContext(Dispatchers.Main) {
                    cartItems = cartItems.filterIndexed{ index, _ -> index != position }
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                    listener?.onCartUpdated()
                }
            }
        }

    }

    override fun getItemCount(): Int = cartItems.size

    fun getCartItems(): List<Pair<CartProduct, Product>> = cartItems
}