package com.example.newfragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.ProductDescription
import com.example.newfragment.data.WishlistProduct
import com.google.android.material.button.MaterialButton
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailsFragment : Fragment() {

    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var wishlistButton: MaterialButton
    private lateinit var addToCartButton: MaterialButton
    private lateinit var specificationsLayout: LinearLayout
    private lateinit var db: AppDatabase  // Екземпляр бази даних
    private var currentProductId: Int = -1 // Ідентифікатор поточного товару
    private var currentUserId: Int = 1 // Замініть на актуальну логіку отримання user ID

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"

        fun newInstance(productId: Int): ProductDetailsFragment {
            val fragment = ProductDetailsFragment()
            val args = Bundle()
            args.putInt(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ініціалізуємо базу даних у onCreate
        db = AppDatabase.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Інфлейтимо макет фрагмента
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)

        // Прив'язуємо елементи макета
        productImage = view.findViewById(R.id.productImage)
        productName = view.findViewById(R.id.productName)
        productPrice = view.findViewById(R.id.productPrice)
        wishlistButton = view.findViewById(R.id.wishlistButton)
        addToCartButton = view.findViewById(R.id.addToCartButton)
        specificationsLayout = view.findViewById(R.id.specificationsLayout)

        // Отримуємо id товару з аргументів та завантажуємо деталі
        currentProductId = requireArguments().getInt(ARG_PRODUCT_ID)
        loadProductDetails(currentProductId)

        // Обробка кліку для додавання товару в кошик
        addToCartButton.setOnClickListener {
            addProductToCart(currentProductId, currentUserId)
        }

        // Обробка кліку для додавання/видалення товару зі списку бажань
        wishlistButton.setOnClickListener {
            addProductToWishlist(currentProductId, currentUserId)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Отримання посилання на тулбар та налаштування кнопки "назад"
        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            // Використовуємо parentFragmentManager для повернення назад
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadProductDetails(productId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val product = db.productDao().getProductById(productId)
            val description: ProductDescription? =
                db.productDescriptionDao().getDescriptionForProduct(productId).firstOrNull()

            withContext(Dispatchers.Main) {
                if (product != null) {
                    // Перетворюємо збережений рядковий URI у об'єкт Uri
                    val imageUri = Uri.parse(product.imageResId)
                    Glide.with(requireContext())
                        .load(imageUri)
                        .into(productImage)

                    productName.text = product.name
                    productPrice.text = String.format("%.2f грн", product.price)
                    // Встановлення шрифтів
                    productName.typeface = ResourcesCompat.getFont(requireContext(), R.font.merriweather_bold)
                    productPrice.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_semibold)
                    wishlistButton.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_semibold)
                    addToCartButton.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_semibold)

                    // Очищення попередніх характеристик
                    specificationsLayout.removeAllViews()
                    description?.let { desc ->
                        addSpecification("Виробник бісеру", desc.beadProducer)
                        addSpecification("Вага", "${desc.weight} г")
                        addSpecification("Країна виробник", desc.countryOfManufacture)
                        addSpecification("Тип бісеру", desc.typeOfBead)
                        addSpecification("Категорія", desc.category)
                        addSpecification("Фурнітура", desc.accessories)
                        addSpecification("Розмір", desc.size)
                    }
                } else {
                    Toast.makeText(requireContext(), "Товар не знайдено", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addProductToCart(productId: Int, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingCartProduct = db.cartProductDao().getCartProduct(userId, productId)

            if (existingCartProduct != null) {
                // Якщо товар уже в кошику, оновлюємо кількість
                val updatedProduct = existingCartProduct.copy(quantity = existingCartProduct.quantity + 1)
                db.cartProductDao().insert(updatedProduct)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Кількість оновлено в кошику", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Якщо товару немає в кошику, додаємо його
                val newCartProduct = CartProduct(userId = userId, productId = productId, quantity = 1)
                db.cartProductDao().insert(newCartProduct)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Додано до кошику", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addProductToWishlist(productId: Int, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingWishlistProduct = db.wishlistProductDao().getWishlistProduct(userId, productId)

            if (existingWishlistProduct != null) {
                // Якщо товар уже в списку бажань, видаляємо його (перемикаємо стан)
                db.wishlistProductDao().delete(existingWishlistProduct)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Видалено зі списку бажань", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Якщо товару немає в списку бажань, додаємо його
                val newWishlistProduct = WishlistProduct(userId = userId, productId = productId)
                db.wishlistProductDao().insert(newWishlistProduct)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Додано до списку бажань", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addSpecification(label: String, value: String) {
        val context = requireContext()

        // Створення контейнера для пари ключ-значення
        val specItemContainer = LinearLayout(context)
        specItemContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        specItemContainer.orientation = LinearLayout.HORIZONTAL

        // Текст для ключа
        val keyTextView = TextView(context)
        keyTextView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        keyTextView.text = "$label:"
        keyTextView.typeface = ResourcesCompat.getFont(context, R.font.montserrat_bold)
        keyTextView.textSize = 14f
        keyTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
        keyTextView.setPadding(0, 0, 8, 16)

        // Текст для значення
        val valueTextView = TextView(context)
        valueTextView.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
        valueTextView.text = value
        valueTextView.typeface = ResourcesCompat.getFont(context, R.font.montserrat_medium)
        valueTextView.textSize = 14f
        valueTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
        valueTextView.setPadding(0, 0, 0, 16)

        // Додаємо елементи до контейнера
        specItemContainer.addView(keyTextView)
        specItemContainer.addView(valueTextView)

        // Додаємо контейнер до основного лейауту характеристик
        specificationsLayout.addView(specItemContainer)
    }
}
