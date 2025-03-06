package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.Product

class CatalogFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        productAdapter = ProductAdapter()
        recyclerView.adapter = productAdapter

        addSampleProducts() // Додаємо товари в БД (тільки якщо їх немає)
        loadProducts() // Завантажуємо товари у RecyclerView

        return view
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val products = db.productDao().getAllProducts()

            requireActivity().runOnUiThread {
                productAdapter.submitList(products)
            }
        }
    }

    private fun addSampleProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val productDao = db.productDao()

            if (productDao.getAllProducts().isEmpty()) {
                val sampleProducts = listOf(
                    Product(
                        name = "Браслет «Українські візерунки»",
                        price = 450.0,
                        beadType = "Китайський бісер",
                        imageResId = R.drawable.bracelet_ukrainian // Використовуємо ID ресурсу
                    ),
                    Product(
                        name = "Браслет «Розмаїття кольорів»",
                        price = 750.0,
                        beadType = "Чеський бісер",
                        imageResId = R.drawable.bracelet_colors
                    ),
                    Product(
                        name = "Браслет «Чорно-білий розмай»",
                        price = 650.0,
                        beadType = "Японський бісер",
                        imageResId = R.drawable.bracelet_black_white
                    )
                )
                productDao.insertAll(*sampleProducts.toTypedArray())
            }
        }
    }


    class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DiffCallback()) {
        class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.ivProductImage)
            val tvName: TextView = view.findViewById(R.id.tvProductName)
            val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
            val tvBeadType: TextView = view.findViewById(R.id.tvBeadType)
            val btnAddToCart: ImageView = view.findViewById(R.id.ivAddToCart)
            val btnWishlist: ImageView = view.findViewById(R.id.ivWishlist)
        }

        class DiffCallback : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
        }


        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = getItem(position)

            holder.tvName.text = product.name
            holder.tvPrice.text = "${product.price} грн"
            holder.tvBeadType.text = product.beadType

            Glide.with(holder.itemView.context)
                .load(product.imageResId) // Передаємо Int замість String
                .into(holder.imageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
            return ProductViewHolder(view)
        }

    }
}

