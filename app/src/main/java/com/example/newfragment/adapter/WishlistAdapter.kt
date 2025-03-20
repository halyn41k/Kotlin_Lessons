package com.example.newfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton // Corrected import
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newfragment.data.Product
import com.example.newfragment.data.WishlistProduct

class WishlistAdapter(
    private var wishlistItems: List<Pair<WishlistProduct, Product>>,
    private val onRemoveClicked: (WishlistProduct) -> Unit,
    private val onMoveToCartClicked: (WishlistProduct) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    class WishlistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.wishlistItemImage)
        val itemName: TextView = view.findViewById(R.id.wishlistItemName)
        val itemPrice: TextView = view.findViewById(R.id.wishlistItemPrice)
        val removeButton: ImageButton = view.findViewById(R.id.removeWishlistItemButton) // Corrected type
        val moveToCartButton: ImageButton = view.findViewById(R.id.moveToCartButton) // Corrected type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.wishlist_item, parent, false)
        return WishlistViewHolder(view)
    }
    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val (wishlistProduct, product) = wishlistItems[position]

        Glide.with(holder.itemView.context)
            .load(product.imageResId)
            .into(holder.itemImage)

        holder.itemName.text = product.name
        holder.itemPrice.text = String.format("%.2f грн", product.price)

        holder.removeButton.setOnClickListener {
            onRemoveClicked(wishlistProduct)
        }

        holder.moveToCartButton.setOnClickListener {
            onMoveToCartClicked(wishlistProduct)
        }
    }

    fun updateData(newWishlistItems: List<Pair<WishlistProduct, Product>>) {
        wishlistItems = newWishlistItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = wishlistItems.size
}