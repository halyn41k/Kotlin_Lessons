package com.example.newfragment

import android.os.Bundle
import android.util.Log
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
import com.example.newfragment.data.Product
import com.example.newfragment.data.ProductDescription
import com.example.newfragment.data.WishlistProduct
import com.google.android.material.button.MaterialButton
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
    private lateinit var db: AppDatabase  // Database instance
    private var currentProductId: Int = -1 // Store the current product ID
    private var currentUserId: Int = 1 // Replace with actual user ID logic

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
        db = AppDatabase.getInstance(requireContext()) // Initialize database in onCreate
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
        specificationsLayout = view.findViewById(R.id.specificationsLayout)

        // Get the product ID.  Store it for later use.
        currentProductId = requireArguments().getInt(ARG_PRODUCT_ID)
        loadProductDetails(currentProductId)

        addToCartButton.setOnClickListener {
            addProductToCart(currentProductId, currentUserId)
        }

        wishlistButton.setOnClickListener {
            addProductToWishlist(currentProductId,currentUserId)
        }

        return view
    }

    private fun loadProductDetails(productId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val product = db.productDao().getProductById(productId)
            val description: ProductDescription? =
                db.productDescriptionDao().getDescriptionForProduct(productId).firstOrNull()

            withContext(Dispatchers.Main) {
                if (product != null) {
                    Glide.with(requireContext())
                        .load(product.imageResId)
                        .into(productImage)

                    productName.text = product.name
                    productPrice.text = String.format("%.2f грн", product.price)
                    //set fonts
                    productName.typeface = ResourcesCompat.getFont(requireContext(), R.font.merriweather_bold)
                    productPrice.typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_semibold)
                    wishlistButton.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_semibold)
                    addToCartButton.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_semibold)

                    specificationsLayout.removeAllViews() // Clear previous views
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
                // Product already in cart, update quantity
                val updatedProduct = existingCartProduct.copy(quantity = existingCartProduct.quantity + 1)
                db.cartProductDao().insert(updatedProduct) //  REPLACE will overwrite
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),"Quantity Updated in Cart", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Product not in cart, add it
                val newCartProduct = CartProduct(userId = userId, productId = productId, quantity = 1)
                db.cartProductDao().insert(newCartProduct)
                withContext(Dispatchers.Main){
                    Toast.makeText(requireContext(),"Added to Cart", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addProductToWishlist(productId: Int, userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val existingWishlistProduct = db.wishlistProductDao().getWishlistProduct(userId, productId)

            if (existingWishlistProduct != null) {
                // Product already in wishlist, remove it (toggle functionality)
                db.wishlistProductDao().delete(existingWishlistProduct)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                    // Change the icon to the "not in wishlist" state
                }
            } else {
                // Product not in wishlist, add it
                val newWishlistProduct = WishlistProduct(userId = userId, productId = productId)
                db.wishlistProductDao().insert(newWishlistProduct)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Added to Wishlist", Toast.LENGTH_SHORT).show()
                    //  Change the icon to the "in wishlist" state
                }
            }
        }
    }

    private fun addSpecification(label: String, value: String) {
        val context = requireContext() // Get context once

        // Create a container for the key-value pair
        val specItemContainer = LinearLayout(context)
        specItemContainer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        specItemContainer.orientation = LinearLayout.HORIZONTAL


        // Create TextView for the key (e.g., "Material:")
        val keyTextView = TextView(context)
        keyTextView.layoutParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        ) // Use weight for distribution
        keyTextView.text = "$label:"
        keyTextView.typeface = ResourcesCompat.getFont(context, R.font.montserrat_bold) // Bold for key
        keyTextView.textSize = 14f  // Use float for textSize
        keyTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
        keyTextView.setPadding(0,0,8,16) // Add padding


        // Create TextView for the value (e.g., "Cotton")
        val valueTextView = TextView(context)
        valueTextView.layoutParams = LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f
        )  // Use weight for distribution
        valueTextView.text = value
        valueTextView.typeface = ResourcesCompat.getFont(context, R.font.montserrat_medium) // Medium for value
        valueTextView.textSize = 14f
        valueTextView.setTextColor(ContextCompat.getColor(context, R.color.black))
        valueTextView.setPadding(0,0,0,16)

        // Add key and value TextViews to the container
        specItemContainer.addView(keyTextView)
        specItemContainer.addView(valueTextView)

        // Add the container to the specificationsLayout
        specificationsLayout.addView(specItemContainer)
    }

}