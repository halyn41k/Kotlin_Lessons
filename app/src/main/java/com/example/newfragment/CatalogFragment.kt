package com.example.newfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.Product
import com.example.newfragment.data.ProductDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        loadProducts()      // Завантажуємо товари у RecyclerView

        return view
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val products = db.productDao().getAllProducts()

            withContext(Dispatchers.Main) {
                productAdapter.submitList(products)
            }
        }
    }

    private fun addSampleProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val productDao = db.productDao()
            val productDescriptionDao = db.productDescriptionDao()

            if (productDao.getAllProducts().isEmpty()) {
                val sampleProducts = listOf(
                    Product(
                        name = "Браслет «Українські візерунки»",
                        price = 450.0,
                        imageResId = R.drawable.bracelet_ukrainian,
                        beadType = "Китайський бісер"
                    ),
                    Product(
                        name = "Браслет «Розмаїття кольорів»",
                        price = 750.0,
                        imageResId = R.drawable.bracelet_colors,
                        beadType = "Японський бісер"
                    ),
                    Product(
                        name = "Браслет «Чорно-білий розмай»",
                        price = 650.0,
                        imageResId = R.drawable.bracelet_black_white,
                        beadType = "Чеський бісер"
                    )
                )
                // insertAll має повертати LongArray з ідентифікаторами вставлених записів
                val productIds: LongArray = productDao.insertAll(*sampleProducts.toTypedArray())

                val descriptions = listOf(
                    ProductDescription(
                        productId = productIds[0].toInt(),
                        beadProducer = "Виробник A",
                        weight = 2.5,
                        countryOfManufacture = "Україна",
                        typeOfBead = "Круглий",
                        category = "Браслети",
                        accessories = "Застібка-карабін",
                        size = "16-18 см"
                    ),
                    ProductDescription(
                        productId = productIds[1].toInt(),
                        beadProducer = "Виробник B",
                        weight = 3.0,
                        countryOfManufacture = "Чехія",
                        typeOfBead = "Овальний",
                        category = "Браслети",
                        accessories = "Магнітна застібка",
                        size = "17-19 см"
                    ),
                    ProductDescription(
                        productId = productIds[2].toInt(),
                        beadProducer = "Виробник C",
                        weight = 2.8,
                        countryOfManufacture = "Японія",
                        typeOfBead = "Циліндричний",
                        category = "Браслети",
                        accessories = "Зав'язки",
                        size = "15-17 см"
                    )
                )
                // Виклик методу insert замість insertProductDescription
                descriptions.forEach { productDescriptionDao.insert(it) }
            }
        }
    }

    inner class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

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

            // Альтернативний підхід: вилучення тексту між символами « та »
            val startIndex = product.name.indexOf('«')
            val endIndex = product.name.indexOf('»')
            val extracted = if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                product.name.substring(startIndex + 1, endIndex)
            } else {
                product.name
            }

            holder.tvName.text = extracted
            holder.tvPrice.text = "${product.price} грн"
            holder.tvBeadType.text = product.beadType

            val context = holder.itemView.context
            val imageResId = imageResourceMap[product.name] ?: R.drawable.default_image

            Log.d("ProductAdapter", "Loading imageResId: $imageResId, Name: ${product.name}")

            Glide.with(context)
                .load(imageResId)
                .into(holder.imageView)

            holder.itemView.setOnClickListener {
                val detailsFragment = ProductDetailsFragment.newInstance(product.id)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit()
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
}

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
        oldItem == newItem
}
