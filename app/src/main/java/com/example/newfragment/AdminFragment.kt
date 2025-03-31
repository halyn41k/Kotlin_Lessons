package com.example.newfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.newfragment.data.AppDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class AdminFragment : Fragment() {

    private lateinit var tvStats: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogout: MaterialButton = view.findViewById(R.id.btnLogout)
        val btnProductList: Button = view.findViewById(R.id.btnProductList)
        val btnOrders: Button = view.findViewById(R.id.btnOrders)
        tvStats = view.findViewById(R.id.tvStats)

        // Динамічне отримання статистики з БД
        updateStats()

        btnLogout.setOnClickListener {
            // Логаут: очищення SharedPreferences
            val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            // Перехід на LoginFragment
            parentFragmentManager.commit {
                replace(R.id.fragment_container, LoginFragment())
            }
        }

        btnProductList.setOnClickListener {
            // Перехід на ProductListFragment
            parentFragmentManager.commit {
                replace(R.id.fragment_container, ProductListFragment())
                addToBackStack(null)
            }
        }

        btnOrders.setOnClickListener {
            // Перехід на OrderFragment
            parentFragmentManager.commit {
                replace(R.id.fragment_container, OrderFragment())
                addToBackStack(null)
            }
        }
    }

    private fun updateStats() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val productDao = db.productDao()
            val orderProductDao = db.orderProductDao()

            // Отримання списків товарів та замовлень
            val products = productDao.getAllProducts()
            val orders = orderProductDao.getAllOrderProducts()

            // Оновлення UI, наприклад, текст у TextView
            tvStats.text = "Товарів: ${products.size}   Замовлень: ${orders.size}"
        }
    }
}
