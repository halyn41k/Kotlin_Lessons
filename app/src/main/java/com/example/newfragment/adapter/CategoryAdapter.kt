package com.example.newfragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.R
import com.bumptech.glide.Glide


data class Category(val name: String, val imageResId: Int)

class CategoryAdapter(private val categories: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lightSquare: View = itemView.findViewById(R.id.lightSquare)
        val darkSquare: View = itemView.findViewById(R.id.darkSquare)
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]

        // Завантаження зображення за допомогою Glide (рекомендовано)
        Glide.with(holder.itemView.context)
            .load(category.imageResId)
            .into(holder.categoryImage)

        holder.categoryName.text = category.name
    }

    override fun getItemCount() = categories.size
}