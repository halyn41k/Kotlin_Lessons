package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newfragment.R
import com.example.newfragment.data.Product

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivProductImage)
        val nameTextView: TextView = itemView.findViewById(R.id.tvProductName)
        val priceTextView: TextView = itemView.findViewById(R.id.tvProductPrice)
        // Якщо клас Product містить властивість beadType – розкоментуйте наступний рядок
        // val beadTypeTextView: TextView = itemView.findViewById(R.id.tvBeadType)
        val addToCartButton: ImageView = itemView.findViewById(R.id.ivAddToCart)
        val wishlistButton: ImageView = itemView.findViewById(R.id.ivWishlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.nameTextView.text = product.name
        holder.priceTextView.text = "₴${product.price}"
        // Якщо Product має властивість beadType – розкоментуйте наступний рядок
        // holder.beadTypeTextView.text = product.beadType

        Glide.with(holder.itemView.context)
            .load(product.imageResId)
            .into(holder.imageView)

        // Обробники кліків для кнопок
        holder.addToCartButton.setOnClickListener {
            // TODO: Додати логіку для додавання в кошик
        }

        holder.wishlistButton.setOnClickListener {
            // TODO: Додати логіку для додавання в список бажань
        }
    }

    override fun getItemCount(): Int = productList.size
}
