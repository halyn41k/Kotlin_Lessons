package com.example.newfragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newfragment.adapter.OrderSummaryAdapter
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.Product
import java.text.NumberFormat
import java.util.Locale

class OrderConfirmationFragment : Fragment() {

    private var confirmationRecyclerView: androidx.recyclerview.widget.RecyclerView? = null
    private var confirmationAdapter: OrderSummaryAdapter? = null
    private var orderIdTextView: TextView? = null
    private var totalAmountTextView: TextView? = null
    private var countdownTextView: TextView? = null
    private var cartItems: List<Pair<CartProduct, Product>> = arrayListOf()
    private var orderId = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_order_confirmation, container, false)
        confirmationRecyclerView = view.findViewById(R.id.confirmationRecyclerView)
        orderIdTextView = view.findViewById(R.id.orderIdTextView)
        totalAmountTextView = view.findViewById(R.id.totalAmountTextView)
        countdownTextView = view.findViewById(R.id.countdownTextView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmationRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        confirmationAdapter = OrderSummaryAdapter(cartItems)
        confirmationRecyclerView?.adapter = confirmationAdapter

        arguments?.let {
            cartItems = it.getSerializable("cartItems") as? List<Pair<CartProduct, Product>> ?: arrayListOf()
            orderId = it.getInt("orderId", -1)
            confirmationAdapter?.updateData(cartItems)
            displayOrderDetails()
        }
        startCountdownTimer()
    }

    private fun displayOrderDetails() {
        orderIdTextView?.text = String.format(Locale.getDefault(), "Номер замовлення: %d", orderId)
        var totalAmount = 0.0
        for ((cartProduct, product) in cartItems) {
            totalAmount += cartProduct.quantity * product.price
        }
        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val formattedTotalAmount = format.format(totalAmount)
        totalAmountTextView?.text = "Загальна сума: $formattedTotalAmount"
    }

    private fun startCountdownTimer() {
        // Відлік 5 секунд
        val timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                countdownTextView?.text = "Перекидаємо через $seconds..."
            }
            override fun onFinish() {
                countdownTextView?.text = "Перекидаємо..."
                // Після завершення таймера виконуємо перехід на OrderListFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, OrderListFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
        timer.start()
    }

    companion object {
        fun newInstance(
            cartItems: ArrayList<Pair<CartProduct?, Product?>?>?,
            orderId: Int
        ): OrderConfirmationFragment {
            val fragment = OrderConfirmationFragment()
            val args = Bundle()
            val safeCartItems = cartItems?.filterNotNull()?.mapNotNull { (cartProduct, product) ->
                cartProduct?.let { c -> product?.let { p -> Pair(c, p) } }
            }?.toCollection(ArrayList()) ?: arrayListOf()
            args.putSerializable("cartItems", safeCartItems)
            args.putInt("orderId", orderId)
            fragment.arguments = args
            return fragment
        }
    }
}
