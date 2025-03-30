package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newfragment.R
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import com.example.newfragment.data.WishlistProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductAdapter(
    private val currentUserId: Int,
    private val onProductClicked: (Int) -> Unit,
    private val onWishlistClicked: (Product) -> Unit,
    private val onAddToCartClicked: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    private val imageResourceMap = mapOf(
        "Браслет «Українські візерунки»" to R.drawable.bracelet_ukrainian,
        "Браслет «Розмаїття кольорів»" to R.drawable.bracelet_colors,
        "Браслет «Чорно-білий розмай»" to R.drawable.bracelet_black_white
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        val context = holder.itemView.context

        val startIndex = product.name.indexOf('«')
        val endIndex = product.name.indexOf('»')
        val extractedName = if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
            product.name.substring(startIndex + 1, endIndex)
        } else {
            product.name
        }

        holder.tvName.text = extractedName
        holder.tvPrice.text = "${product.price} грн"
        holder.tvBeadType.text = product.beadType

        val imageResId = imageResourceMap[product.name] ?: R.drawable.default_image

        Glide.with(context)
            .load(imageResId)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onProductClicked(product.id)
        }

        holder.btnWishlist.setImageResource(
            if (product.isInWishlist) R.drawable.filled_heart else R.drawable.ic_wishlists
        )

        holder.btnWishlist.setOnClickListener {
            onWishlistClicked(product)
        }

        holder.btnAddToCart.setOnClickListener {
            onAddToCartClicked(product)
        }
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.ivProductImage)
        val tvName: TextView = view.findViewById(R.id.tvProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val btnAddToCart: ImageView = view.findViewById(R.id.ivAddToCart)
        val btnWishlist: ImageView = view.findViewById(R.id.ivWishlist)
        val tvBeadType: TextView = view.findViewById(R.id.tvBeadType)
    }
}

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem && oldItem.isInWishlist == newItem.isInWishlist
}