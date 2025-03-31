package com.example.newfragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newfragment.adapter.OrderCardsAdapter
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.databinding.FragmentOrderBinding
import com.example.newfragment.data.OrderProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderFragment : Fragment(), OrderCardsAdapter.OnStatusChangeListener {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var adapter: OrderCardsAdapter

    // Приклад – завантаження замовлень для поточного користувача
    private val currentUserId = 1 // Замініть на реальну логіку отримання користувача

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Обробка кнопки повернення назад
        val btnBack: ImageButton = binding.root.findViewById(com.example.newfragment.R.id.btnBack)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrderCardsAdapter(this)
        binding.ordersRecyclerView.adapter = adapter

        loadOrders()
    }

    private fun loadOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            // Отримуємо всі замовлення з БД. При потребі можна додати фільтрацію за користувачем.
            val orders: List<OrderProduct> = db.orderProductDao().getAllOrderProducts()
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

    // Реалізація інтерфейсу для зміни статусу
    override fun onChangeStatusClicked(order: OrderProduct) {
        // Відобразимо AlertDialog з варіантами
        val statusOptions = arrayOf("оформлено", "виконано", "скасовано")
        AlertDialog.Builder(requireContext())
            .setTitle("Оберіть новий статус")
            .setItems(statusOptions) { dialog, which ->
                val newStatus = statusOptions[which]
                updateOrderStatus(order, newStatus)
            }
            .setNegativeButton("Відміна", null)
            .show()
    }

    private fun updateOrderStatus(order: OrderProduct, newStatus: String) {
        CoroutineScope(Dispatchers.IO).launch {
            db.orderProductDao().updateOrderStatus(order.id, newStatus)
            // Після оновлення, перезавантажуємо список замовлень
            val orders: List<OrderProduct> = db.orderProductDao().getAllOrderProducts()
            withContext(Dispatchers.Main) {
                adapter.submitList(orders)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
