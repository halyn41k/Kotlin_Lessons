package com.example.newfragment.adapter // Make sure this package declaration is correct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newfragment.R
import com.example.newfragment.data.Product
import com.example.newfragment.data.WishlistProduct
import com.example.newfragment.databinding.WishlistItemBinding // Import View Binding

class WishlistAdapter(
    private var wishlistItems: List<Pair<WishlistProduct, Product>>,
    private val onRemoveClicked: (WishlistProduct) -> Unit,
    private val onMoveToCartClicked: (WishlistProduct) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: WishlistItemBinding) : RecyclerView.ViewHolder(binding.root) // Use View Binding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = WishlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false) // Use View Binding
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (wishlistProduct, product) = wishlistItems[position]

        Glide.with(holder.itemView.context)
            .load(product.imageResId)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
            .into(holder.binding.wishlistItemImage) // Access views through binding

        holder.binding.wishlistItemName.text = product.name
        holder.binding.wishlistItemPrice.text = product.price.toString()

        holder.binding.removeWishlistItemButton.setOnClickListener {
            onRemoveClicked(wishlistProduct)
        }

        holder.binding.moveToCartButton.setOnClickListener {
            onMoveToCartClicked(wishlistProduct)
        }
    }
    fun updateData(newWishlistItems: List<Pair<WishlistProduct, Product>>) {
        wishlistItems = newWishlistItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = wishlistItems.size
}