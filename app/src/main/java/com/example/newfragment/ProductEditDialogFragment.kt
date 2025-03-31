package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.Product
import com.example.newfragment.data.ProductDescription
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductEditDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"
        fun newInstance(productId: Int): ProductEditDialogFragment {
            val fragment = ProductEditDialogFragment()
            val args = Bundle()
            args.putInt(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }

    private var productId: Int = 0

    // Поля для товару
    private lateinit var etProductName: EditText
    private lateinit var etProductPrice: EditText
    private lateinit var etBeadType: EditText

    // Поля для опису товару
    private lateinit var etBeadProducer: EditText
    private lateinit var etWeight: EditText
    private lateinit var etCountryOfManufacture: EditText
    private lateinit var etTypeOfBead: EditText
    private lateinit var etCategory: EditText
    private lateinit var etAccessories: EditText
    private lateinit var etSize: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getInt(ARG_PRODUCT_ID) ?: 0
        // Використання стилю діалогу
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_edit_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Знаходимо всі елементи
        etProductName = view.findViewById(R.id.etProductName)
        etProductPrice = view.findViewById(R.id.etProductPrice)
        etBeadType = view.findViewById(R.id.etBeadType)

        etBeadProducer = view.findViewById(R.id.etBeadProducer)
        etWeight = view.findViewById(R.id.etWeight)
        etCountryOfManufacture = view.findViewById(R.id.etCountryOfManufacture)
        etTypeOfBead = view.findViewById(R.id.etTypeOfBead)
        etCategory = view.findViewById(R.id.etCategory)
        etAccessories = view.findViewById(R.id.etAccessories)
        etSize = view.findViewById(R.id.etSize)

        val btnSave: Button = view.findViewById(R.id.btnSave)
        val btnCancel: Button = view.findViewById(R.id.btnCancel)

        // Завантаження даних товару та опису з бази даних
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val product: Product? = db.productDao().getProductById(productId)
            val productDescription: ProductDescription? = db.productDao().getProductDescription(productId)
            withContext(Dispatchers.Main) {
                product?.let { p ->
                    etProductName.setText(p.name)
                    etProductPrice.setText(p.price.toString())
                    etBeadType.setText(p.beadType)
                }
                productDescription?.let { pd ->
                    etBeadProducer.setText(pd.beadProducer)
                    etWeight.setText(pd.weight.toString())
                    etCountryOfManufacture.setText(pd.countryOfManufacture)
                    etTypeOfBead.setText(pd.typeOfBead)
                    etCategory.setText(pd.category)
                    etAccessories.setText(pd.accessories)
                    etSize.setText(pd.size)
                }
            }
        }

        btnSave.setOnClickListener {
            // Зчитування значень з усіх полів
            val updatedName = etProductName.text.toString().trim()
            val updatedPrice = etProductPrice.text.toString().toDoubleOrNull() ?: 0.0
            val updatedBeadType = etBeadType.text.toString().trim()

            val updatedBeadProducer = etBeadProducer.text.toString().trim()
            val updatedWeight = etWeight.text.toString().toDoubleOrNull() ?: 0.0
            val updatedCountry = etCountryOfManufacture.text.toString().trim()
            val updatedTypeOfBead = etTypeOfBead.text.toString().trim()
            val updatedCategory = etCategory.text.toString().trim()
            val updatedAccessories = etAccessories.text.toString().trim()
            val updatedSize = etSize.text.toString().trim()

            // Оновлення товару та опису в базі даних
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(requireContext())
                val oldProduct = db.productDao().getProductById(productId)
                if (oldProduct != null) {
                    val updatedProduct = oldProduct.copy(
                        name = updatedName,
                        price = updatedPrice,
                        beadType = updatedBeadType,
                        updatedAt = System.currentTimeMillis()
                    )
                    db.productDao().insertProduct(updatedProduct)

                    // Якщо запис опису існує, зберігаємо його id; інакше встановлюємо 0 для нового запису
                    val oldDescription = db.productDao().getProductDescription(productId)
                    val updatedDescription = ProductDescription(
                        id = oldDescription?.id ?: 0,
                        productId = productId,
                        beadProducer = updatedBeadProducer,
                        weight = updatedWeight,
                        countryOfManufacture = updatedCountry,
                        typeOfBead = updatedTypeOfBead,
                        category = updatedCategory,
                        accessories = updatedAccessories,
                        size = updatedSize
                    )
                    db.productDao().insertProductDescription(updatedDescription)
                    withContext(Dispatchers.Main) {
                        dismiss()
                    }
                }
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }
}
