package com.example.newfragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import com.example.newfragment.data.ProductDescription
import com.example.newfragment.data.WishlistProduct
import com.example.newfragment.viewmodel.WishlistViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CatalogFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var etSearch: EditText
    private var currentUserId = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        productAdapter = ProductAdapter()
        recyclerView.adapter = productAdapter

        wishlistViewModel = ViewModelProvider(this)[WishlistViewModel::class.java]

        addSampleProducts()
        loadProducts("") // завантажуємо усі товари за замовчуванням

        setupSearch()

        return view
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Виконуємо пошук після зміни тексту
                val query = s.toString().trim()
                loadProducts(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // query: якщо пусто, завантажуємо всі товари, інакше – шукаємо
    private fun loadProducts(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val products = if (query.isEmpty()) {
                db.productDao().getAllProducts()
            } else {
                db.productDao().searchProducts(query)
            }
            // Отримуємо список продуктів з wishlist для поточного користувача
            val wishlist = wishlistViewModel.getWishlistForUser(currentUserId).value ?: emptyList()
            // Оновлюємо кожен продукт за даними wishlist
            val updatedProducts = products.map { product ->
                product.copy(isInWishlist = wishlist.any { wishlistItem -> wishlistItem.productId == product.id })
            }
            withContext(Dispatchers.Main) {
                productAdapter.submitList(updatedProducts)
            }
        }
    }

    // Існуючий метод addSampleProducts без змін
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
                val productIds = productDao.insertAll(*sampleProducts.toTypedArray())

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
                descriptions.forEach { productDescriptionDao.insert(it) }
            }
        }
    }

    // Решта коду адаптера залишається без змін
    inner class ProductAdapter :
        ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {
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
            val db = AppDatabase.getInstance(holder.itemView.context)

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

            val context = holder.itemView.context
            val imageResId = imageResourceMap[product.name] ?: R.drawable.default_image

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

            holder.btnWishlist.setImageResource(
                if (product.isInWishlist) R.drawable.filled_heart else R.drawable.ic_wishlists
            )

            holder.btnWishlist.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val isInWishlist = db.wishlistProductDao().isInWishlist(
                        currentUserId,
                        product.id
                    )
                    val wishlistProduct =
                        WishlistProduct(userId = currentUserId, productId = product.id)

                    if (isInWishlist) {
                        wishlistViewModel.removeFromWishlist(wishlistProduct)
                    } else {
                        wishlistViewModel.addToWishlist(wishlistProduct)
                    }

                    withContext(Dispatchers.Main) {
                        product.isInWishlist = !product.isInWishlist

                        holder.btnWishlist.setImageResource(
                            if (product.isInWishlist) R.drawable.filled_heart else R.drawable.ic_wishlists
                        )

                        if (!product.isInWishlist) {
                            Toast.makeText(context, "Видалено з бажаного: $extractedName", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Додано до бажаного: $extractedName", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }

            holder.btnAddToCart.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val existingCartItem = db.cartProductDao().getProduct(
                        currentUserId,
                        product.id
                    )
                    if (existingCartItem != null) {
                        val updatedItem =
                            existingCartItem.copy(quantity = existingCartItem.quantity + 1)
                        db.cartProductDao().update(updatedItem)
                    } else {
                        db.cartProductDao()
                            .insert(CartProduct(userId = currentUserId, productId = product.id, quantity = 1))
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Додано в кошик: $extractedName", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
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
        oldItem == newItem && oldItem.isInWishlist == newItem.isInWishlist
}