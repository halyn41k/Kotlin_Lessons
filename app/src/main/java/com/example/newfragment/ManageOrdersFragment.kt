package com.example.newfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newfragment.adapter.OrderAdapter
import com.example.newfragment.databinding.FragmentManageOrdersBinding
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.OrderProduct
import kotlinx.coroutines.launch

class ManageOrdersFragment : Fragment() {
    private var _binding: FragmentManageOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Використовуємо анонімний об’єкт для обробки кліків
        orderAdapter = OrderAdapter(object : OrderAdapter.OnOrderActionListener {
            override fun onConfirmOrder(order: OrderProduct) {
                lifecycleScope.launch {
                    val newStatus = "completed"
                    val updatedOrder = order.copy(status = newStatus)
                    AppDatabase.getInstance(requireContext()).orderProductDao().update(updatedOrder)
                    loadOrders()
                }
            }

            override fun onCancelOrder(order: OrderProduct) {
                lifecycleScope.launch {
                    val newStatus = "cancelled"
                    val updatedOrder = order.copy(status = newStatus)
                    AppDatabase.getInstance(requireContext()).orderProductDao().update(updatedOrder)
                    loadOrders()
                }
            }
        })

        binding.recyclerViewOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
        loadOrders()
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            // Замініть метод на той, що є у вашому DAO, наприклад, getAllOrderProducts()
            val orders: List<OrderProduct> = AppDatabase.getInstance(requireContext()).orderProductDao().getAllOrderProducts()
            orderAdapter.submitList(orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
