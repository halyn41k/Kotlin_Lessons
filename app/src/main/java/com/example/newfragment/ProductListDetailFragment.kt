package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.newfragment.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductListDetailFragment : Fragment() {

    private var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getInt("product_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val ivProductImage: ImageView = view.findViewById(R.id.ivProductImage)

        // TextView для опису товару
        val tvBeadProducer: TextView = view.findViewById(R.id.tvBeadProducer)
        val tvWeight: TextView = view.findViewById(R.id.tvWeight)
        val tvCountry: TextView = view.findViewById(R.id.tvCountryOfManufacture)
        val tvTypeOfBead: TextView = view.findViewById(R.id.tvTypeOfBead)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvAccessories: TextView = view.findViewById(R.id.tvAccessories)
        val tvSize: TextView = view.findViewById(R.id.tvSize)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            // Завантаження базової інформації товару
            val product = db.productDao().getProductById(productId)
            // Завантаження опису товару
            val description = db.productDao().getProductDescription(productId)

            requireActivity().runOnUiThread {
                product?.let {
                    tvProductName.text = it.name
                    tvProductPrice.text = "$${it.price}"
                    Glide.with(requireContext())
                        .load(it.imageResId)
                        .placeholder(R.drawable.default_image)
                        .into(ivProductImage)
                }
                description?.let {
                    tvBeadProducer.text = "Виробник: ${it.beadProducer}"
                    tvWeight.text = "Вага: ${it.weight}"
                    tvCountry.text = "Країна виробництва: ${it.countryOfManufacture}"
                    tvTypeOfBead.text = "Тип намиста: ${it.typeOfBead}"
                    tvCategory.text = "Категорія: ${it.category}"
                    tvAccessories.text = "Фурнітура: ${it.accessories}"
                    tvSize.text = "Розмір: ${it.size}"
                }
            }
        }
    }

    companion object {
        fun newInstance(productId: Int) = ProductListDetailFragment().apply {
            arguments = Bundle().apply {
                putInt("product_id", productId)
            }
        }
    }
}
