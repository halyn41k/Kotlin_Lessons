package com.example.newfragment

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.newfragment.R
import com.example.newfragment.databinding.FragmentAddProductBinding
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.Product
import com.example.newfragment.data.ProductDescription
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddProduct.setOnClickListener {
            val name = binding.etName.text.toString()
            val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val beadType = binding.etBeadType.text.toString()

            // Дані опису продукту
            val beadProducer = binding.etBeadProducer.text.toString()
            val weight = binding.etWeight.text.toString().toDoubleOrNull() ?: 0.0
            val country = binding.etCountryOfManufacture.text.toString()
            val typeOfBead = binding.etTypeOfBead.text.toString()
            val category = binding.etCategory.text.toString()
            val accessories = binding.etAccessories.text.toString()
            val size = binding.etSize.text.toString()

            // Створення об'єкту продукту
            // Зображення за замовчуванням – замініть R.drawable.default_product_image на потрібний ресурс
            val product = Product(name = name, price = price, imageResId = R.drawable.default_image, beadType = beadType)
            lifecycleScope.launch {
                // Вставка продукту та отримання його id
                val productId = AppDatabase.getInstance(requireContext()).productDao().insert(product).toInt()
                // Створення опису продукту з отриманим id
                val description = ProductDescription(
                    productId = productId,
                    beadProducer = beadProducer,
                    weight = weight,
                    countryOfManufacture = country,
                    typeOfBead = typeOfBead,
                    category = category,
                    accessories = accessories,
                    size = size
                )
                AppDatabase.getInstance(requireContext()).productDescriptionDao().insert(description)
                Toast.makeText(requireContext(), "Товар додано", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
