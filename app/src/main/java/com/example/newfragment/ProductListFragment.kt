package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductTableAdapter
    private var products: List<Product> = listOf()

    // Стан сортування для кожного стовпця
    private var sortByIdAsc = true
    private var sortByNameAsc = true
    private var sortByPriceAsc = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Кнопка "Назад"
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Контейнер кнопки "Додати товар" (і обидва елементи всередині нього)
        val addProductContainer: LinearLayout = view.findViewById(R.id.addProductContainer)
        addProductContainer.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, ProductAddFragment())
                addToBackStack(null)
            }
        }

        // Заголовки таблиці для сортування
        val tvHeaderId: TextView = view.findViewById(R.id.tvHeaderId)
        val tvHeaderName: TextView = view.findViewById(R.id.tvHeaderName)
        val tvHeaderPrice: TextView = view.findViewById(R.id.tvHeaderPrice)

        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Ініціалізація адаптера із обробниками кліків:
        // onClick – відкриває фрагмент з деталями товару
        // onEdit – відкриває модальне вікно для редагування товару з попередньо заповненими даними
        // onDelete – видаляє товар із БД
        productAdapter = ProductTableAdapter(
            products,
            onClick = { product ->
                val fragment = ProductListDetailFragment.newInstance(product.id)
                parentFragmentManager.commit {
                    replace(R.id.fragment_container, fragment)
                    addToBackStack(null)
                }
            },
            onEdit = { product ->
                // Виклик функції для відкриття модального вікна редагування
                showEditProductDialog(product)
            },
            onDelete = { product ->
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getInstance(requireContext())
                    db.productDao().deleteProduct(product)
                    products = db.productDao().getAllProducts()
                    requireActivity().runOnUiThread {
                        productAdapter.updateProducts(products)
                    }
                }
            }
        )
        recyclerView.adapter = productAdapter

        // Завантаження товарів з БД
        loadProducts()

        // Обробка кліків на заголовках для сортування
        tvHeaderId.setOnClickListener {
            products = if (sortByIdAsc) {
                products.sortedBy { it.id }
            } else {
                products.sortedByDescending { it.id }
            }
            sortByIdAsc = !sortByIdAsc
            productAdapter.updateProducts(products)
        }
        tvHeaderName.setOnClickListener {
            products = if (sortByNameAsc) {
                products.sortedBy { it.name }
            } else {
                products.sortedByDescending { it.name }
            }
            sortByNameAsc = !sortByNameAsc
            productAdapter.updateProducts(products)
        }
        tvHeaderPrice.setOnClickListener {
            products = if (sortByPriceAsc) {
                products.sortedBy { it.price }
            } else {
                products.sortedByDescending { it.price }
            }
            sortByPriceAsc = !sortByPriceAsc
            productAdapter.updateProducts(products)
        }
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            products = db.productDao().getAllProducts()
            requireActivity().runOnUiThread {
                productAdapter.updateProducts(products)
            }
        }
    }

    // Функція для відображення модального вікна редагування товару
    private fun showEditProductDialog(product: Product) {
        // Приклад використання DialogFragment для редагування
        val editDialog = ProductEditDialogFragment.newInstance(product.id)
        editDialog.show(childFragmentManager, "ProductEditDialog")
    }
}
