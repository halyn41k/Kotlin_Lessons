package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.databinding.FragmentOrderListBinding
import com.example.newfragment.adapter.OrderAdapter
import com.example.newfragment.data.OrderProduct // Імпортуємо OrderProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderListFragment : Fragment() {

    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: OrderAdapter

    // Видаляємо OnOrderActionListener, оскільки він не використовується в цьому фрагменті
    private val currentUserId = 1  // Замініть на логіку отримання поточного користувача

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderListBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrderAdapter(object : OrderAdapter.OnOrderActionListener {
            override fun onConfirmOrder(order: OrderProduct) {
                // Реалізуйте логіку обробки підтвердження замовлення (якщо потрібно)
            }

            override fun onCancelOrder(order: OrderProduct) {
                // Реалізуйте логіку обробки скасування замовлення (якщо потрібно)
            }
        }) // Передаємо OnOrderActionListener
        binding.ordersRecyclerView.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            // Отримання замовлень (платежів) поточного користувача з PaymentDao
            val orders = db.paymentDao().getOrderProductsByUser(currentUserId)
            withContext(Dispatchers.Main) {
                if (orders.isEmpty()) {
                    binding.noOrdersTextView.visibility = View.VISIBLE
                    binding.ordersRecyclerView.visibility = View.GONE
                } else {
                    binding.noOrdersTextView.visibility = View.GONE
                    binding.ordersRecyclerView.visibility = View.VISIBLE
                    adapter.submitList(orders)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}