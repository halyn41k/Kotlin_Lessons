package com.example.newfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.data.Product

class ProductTableAdapter(
    private var products: List<Product>,
    private val onClick: (Product) -> Unit,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductTableAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductId: TextView = itemView.findViewById(R.id.tvProductId)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        // Кнопка для редагування (переконайтеся, що вона є в XML розмітці елемента)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_table, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.tvProductId.text = product.id.toString()
        holder.tvProductName.text = product.name
        // Ціна відображається без символу валюти
        holder.tvProductPrice.text = product.price.toString()

        // Обробка кліку на рядку для перегляду деталей
        holder.itemView.setOnClickListener { onClick(product) }
        // Обробка кліку на кнопці редагування
        holder.ivEdit.setOnClickListener { onEdit(product) }
        // Обробка кліку на кнопці видалення
        holder.ivDelete.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                .setTitle("Підтвердження видалення")
                .setMessage("Ви впевнені, що хочете видалити?")
                .setPositiveButton("Так") { dialog, which ->
                    onDelete(product)
                }
                .setNegativeButton("Ні") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
