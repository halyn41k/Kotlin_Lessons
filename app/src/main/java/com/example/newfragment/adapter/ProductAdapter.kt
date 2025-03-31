package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newfragment.data.Product
import com.example.newfragment.databinding.ItemProductBinding

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Використовуємо inner class з View Binding
    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Інфлюємо розмітку через binding
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        with(holder.binding) {
            // Встановлюємо дані товару
            tvProductName.text = product.name
            tvProductPrice.text = "₴${product.price}"
            Glide.with(root.context)
                .load(product.imageResId)
                .into(ivProductImage)

            // Обробники кліків для кнопок
            ivAddToCart.setOnClickListener {
                // TODO: Додати логіку додавання товару в кошик
            }
            ivWishlist.setOnClickListener {
                // TODO: Додати логіку додавання товару до списку бажань
            }
        }
    }

    override fun getItemCount(): Int = productList.size
}


