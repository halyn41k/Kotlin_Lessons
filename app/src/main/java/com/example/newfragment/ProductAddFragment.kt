package com.example.newfragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.Product
import com.example.newfragment.data.ProductDescription
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductAddFragment : Fragment() {

    // Поля для Product
    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    // Замінюємо ImageView на MaterialButton для фото
    private lateinit var btnAddPhoto: MaterialButton
    private lateinit var etBeadType: EditText

    // Поля для ProductDescription
    private lateinit var etBeadProducer: EditText
    private lateinit var etWeight: EditText
    private lateinit var etCountry: EditText
    private lateinit var etTypeOfBead: EditText
    private lateinit var etCategory: EditText
    private lateinit var etAccessories: EditText
    private lateinit var etSize: EditText

    private lateinit var btnSave: Button
    private lateinit var btnCancel: MaterialButton

    // URI вибраного фото
    private var selectedImageUri: Uri? = null

    // Реєстрація для отримання результату вибору фото з галереї
    private val getImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    // Отримуємо прапорці дозволу та зберігаємо persistable дозвіл
                    val flags = result.data?.flags?.and(Intent.FLAG_GRANT_READ_URI_PERMISSION) ?: 0
                    try {
                        requireContext().contentResolver.takePersistableUriPermission(uri, flags)
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    btnAddPhoto.text = "Фото вибране"
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ініціалізація полів для Product
        etName = view.findViewById(R.id.etProductName)
        etPrice = view.findViewById(R.id.etProductPrice)
        btnAddPhoto = view.findViewById(R.id.btnAddPhoto)
        etBeadType = view.findViewById(R.id.etBeadType)

        // Ініціалізація полів для ProductDescription
        etBeadProducer = view.findViewById(R.id.etBeadProducer)
        etWeight = view.findViewById(R.id.etWeight)
        etCountry = view.findViewById(R.id.etCountryOfManufacture)
        etTypeOfBead = view.findViewById(R.id.etTypeOfBead)
        etCategory = view.findViewById(R.id.etCategory)
        etAccessories = view.findViewById(R.id.etAccessories)
        etSize = view.findViewById(R.id.etSize)

        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // При кліку на кнопку додавання фото відкриваємо галерею
        btnAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getImage.launch(intent)
        }

        btnSave.setOnClickListener {
            // Зчитування даних з полів
            val name = etName.text.toString().trim()
            val priceText = etPrice.text.toString().trim()
            val beadType = etBeadType.text.toString().trim()

            val beadProducer = etBeadProducer.text.toString().trim()
            val weightText = etWeight.text.toString().trim()
            val country = etCountry.text.toString().trim()
            val typeOfBead = etTypeOfBead.text.toString().trim()
            val category = etCategory.text.toString().trim()
            val accessories = etAccessories.text.toString().trim()
            val size = etSize.text.toString().trim()

            // Перевірка, що всі поля заповнені та фото вибране
            if (name.isEmpty() || priceText.isEmpty() || beadType.isEmpty() ||
                beadProducer.isEmpty() || weightText.isEmpty() || country.isEmpty() ||
                typeOfBead.isEmpty() || category.isEmpty() || accessories.isEmpty() ||
                size.isEmpty() || selectedImageUri == null
            ) {
                Toast.makeText(requireContext(), "Заповніть усі поля та оберіть фото", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceText.toDoubleOrNull() ?: run {
                Toast.makeText(requireContext(), "Некоректна ціна", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val weight = weightText.toDoubleOrNull() ?: run {
                Toast.makeText(requireContext(), "Некоректна вага", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Для збереження фото з галереї зберігаємо його URI як рядок
            val imageUriString = selectedImageUri.toString()

            // Створення нового об'єкта Product
            val newProduct = Product(
                name = name,
                price = price,
                imageResId = imageUriString, // передаємо збережений рядковий URI
                beadType = beadType
            )


            // Запис у БД: спочатку додаємо товар, потім опис
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getInstance(requireContext())
                db.productDao().insertProduct(newProduct)
                val savedProduct = db.productDao().getAllProducts().lastOrNull()
                if (savedProduct != null) {
                    val description = ProductDescription(
                        productId = savedProduct.id,
                        beadProducer = beadProducer,
                        weight = weight,
                        countryOfManufacture = country,
                        typeOfBead = typeOfBead,
                        category = category,
                        accessories = accessories,
                        size = size
                    )
                    db.productDao().insertProductDescription(description)
                }
                requireActivity().runOnUiThread {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }
}
