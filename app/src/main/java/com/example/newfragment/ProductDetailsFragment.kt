package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.Product
import com.example.newfragment.data.ProductDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductDetailsFragment : Fragment() {

    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var wishlistButton: Button
    private lateinit var addToCartButton: Button
    private lateinit var descriptionLayout: LinearLayout  // Для відображення опису продукту
    private var loadedProduct: Product? = null

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)

        productImage = view.findViewById(R.id.productImage)
        productName = view.findViewById(R.id.productName)
        productPrice = view.findViewById(R.id.productPrice)
        wishlistButton = view.findViewById(R.id.wishlistButton)
        addToCartButton = view.findViewById(R.id.addToCartButton)
        descriptionLayout = view.findViewById(R.id.descriptionLayout)

        val productId = requireArguments().getInt(ARG_PRODUCT_ID)
        loadProductDetails(productId)

        addToCartButton.setOnClickListener {
            Toast.makeText(requireContext(), "Товар додано в кошик", Toast.LENGTH_SHORT).show()
        }

        wishlistButton.setOnClickListener {
            Toast.makeText(requireContext(), "Товар додано в список бажань", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadProductDetails(productId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            // Отримуємо продукт за id
            val product = db.productDao().getProductById(productId)
            loadedProduct = product

            // Отримуємо опис продукту (беремо перший, якщо їх декілька)
            val description: ProductDescription? =
                db.productDescriptionDao().getDescriptionForProduct(productId).firstOrNull()

            withContext(Dispatchers.Main) {
                if (product != null) {
                    Glide.with(requireContext())
                        .load(product.imageResId)
                        .into(productImage)

                    productName.text = product.name
                    productPrice.text = String.format("%.2f грн", product.price)

                    // Відображення детальних характеристик з ProductDescription
                    descriptionLayout.removeAllViews()
                    description?.let { desc: ProductDescription ->
                        addDescriptionTextView("Виробник бісеру", desc.beadProducer)
                        addDescriptionTextView("Вага", "${desc.weight} г")
                        addDescriptionTextView("Країна виробник", desc.countryOfManufacture)
                        addDescriptionTextView("Тип бісеру", desc.typeOfBead)
                        addDescriptionTextView("Категорія", desc.category)
                        addDescriptionTextView("Фурнітура", desc.accessories)
                        addDescriptionTextView("Розмір", desc.size)
                    }
                } else {
                    Toast.makeText(requireContext(), "Товар не знайдено", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addDescriptionTextView(label: String, value: String) {
        val textView = TextView(requireContext())
        textView.text = "$label: $value"
        textView.setPadding(8, 4, 8, 4)
        descriptionLayout.addView(textView)
    }
}
