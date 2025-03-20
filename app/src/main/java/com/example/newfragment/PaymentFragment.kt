package com.example.newfragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.OrderProduct
import com.example.newfragment.data.Payment
import com.example.newfragment.data.Product
import com.example.newfragment.databinding.FragmentPaymentBinding
import com.example.newfragment.adapter.OrderSummaryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import java.util.Random

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderSummaryAdapter: OrderSummaryAdapter
    private lateinit var db: AppDatabase

    private var cartItems = ArrayList<Pair<CartProduct, Product>>()
    private val currentUserId = 1
    private var orderId: Int = generateOrderId()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        db = AppDatabase.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.orderSummaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Отримуємо список товарів із аргументів (за допомогою Bundle)
        arguments?.getSerializable("cartItems")?.let {
            cartItems = it as ArrayList<Pair<CartProduct, Product>>
        }

        orderSummaryAdapter = OrderSummaryAdapter(cartItems)
        binding.orderSummaryRecyclerView.adapter = orderSummaryAdapter
        orderSummaryAdapter.updateData(cartItems)
        calculateAndDisplayTotalPrice()

        binding.cardPaymentLayout.visibility = View.GONE

        binding.paymentMethodRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioCard -> binding.cardPaymentLayout.visibility = View.VISIBLE
                R.id.radioCashOnDelivery -> binding.cardPaymentLayout.visibility = View.GONE
            }
        }

        // Викликаємо налаштування форматування вводу
        setupInputFormatting()

        binding.payButton.setOnClickListener {
            when (binding.paymentMethodRadioGroup.checkedRadioButtonId) {
                R.id.radioCard -> {
                    if (validateCardDetails()) {
                        savePaymentAndNavigate("card", "completed")
                    }
                }
                R.id.radioCashOnDelivery -> savePaymentAndNavigate("cash", "pending")
                else -> Toast.makeText(
                    requireContext(),
                    "Будь ласка, оберіть спосіб оплати",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Налаштовуємо форматування вводу для номеру картки та терміну дії.
     */
    private fun setupInputFormatting() {
        // Форматування номера картки: групування по 4 цифри
        binding.cardNumberEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var previousText = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                val currentText = s.toString()
                // Якщо текст вже відформатовано, не переформатовуємо
                if (currentText == previousText) return
                isFormatting = true
                // Видаляємо всі пробіли
                val digits = currentText.replace("\\s".toRegex(), "")
                // Групуємо по 4 цифри
                val formatted = digits.chunked(4).joinToString(" ")
                binding.cardNumberEditText.setText(formatted)
                // Встановлюємо курсор в кінець
                binding.cardNumberEditText.setSelection(formatted.length)
                previousText = formatted
                isFormatting = false
            }
        })

        // Форматування терміну дії: автоматично вставляємо "/" після 2 цифр
        binding.expiryDateEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var previousText = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                val currentText = s.toString()
                if (currentText == previousText) return
                isFormatting = true
                // Видаляємо символ "/"
                val digits = currentText.replace("/", "")
                val formatted = if (digits.length >= 2) {
                    // Якщо більше 2 символів, вставляємо "/" після перших 2
                    digits.substring(0, 2) + "/" + digits.substring(2).take(2)
                } else {
                    digits
                }
                binding.expiryDateEditText.setText(formatted)
                binding.expiryDateEditText.setSelection(formatted.length)
                previousText = formatted
                isFormatting = false
            }
        })
    }

    private fun savePaymentAndNavigate(paymentMethod: String, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            savePaymentToDatabase(paymentMethod, status)
            withContext(Dispatchers.Main) {
                val orderConfirmationFragment = OrderConfirmationFragment.newInstance(ArrayList(cartItems), orderId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, orderConfirmationFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun generateOrderId(): Int {
        return Random().nextInt(1000000)
    }

    private fun calculateAndDisplayTotalPrice() {
        var totalPrice = 0.0
        for ((cartProduct, product) in cartItems) {
            totalPrice += product.price * cartProduct.quantity
        }
        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val formattedTotalPrice = format.format(totalPrice)
        binding.totalPriceTextView.text = "До сплати: $formattedTotalPrice"
    }

    private fun savePaymentToDatabase(paymentMethod: String, status: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var totalPrice = 0.0
            for ((cartProduct, product) in cartItems) {
                totalPrice += product.price * cartProduct.quantity
            }
            // Створюємо запис платежу
            val payment = Payment(
                userId = currentUserId,
                orderId = orderId,
                amount = totalPrice,
                paymentMethod = paymentMethod,
                status = status
            )
            db.paymentDao().insert(payment)

            // Додаємо записи у таблицю order_product для кожного товару
            for ((cartProduct, product) in cartItems) {
                val orderProduct = OrderProduct(
                    orderId = orderId,
                    productId = product.id,
                    quantity = cartProduct.quantity,
                    price = product.price
                )
                db.orderProductDao().insert(orderProduct)
            }

            // Видаляємо товари з кошика після успішної оплати
            for ((cartProduct, _) in cartItems) {
                db.cartProductDao().delete(cartProduct)
            }
        }
    }

    private fun validateCardDetails(): Boolean {
        val cardNumber = binding.cardNumberEditText.text.toString().trim().replace("\\s".toRegex(), "")
        val expiryDate = binding.expiryDateEditText.text.toString().trim()
        val cvv = binding.cvvEditText.text.toString().trim()

        // Перевірка номера картки: не порожній, складається лише з цифр, довжина від 13 до 19, проходить алгоритм Луна
        if (cardNumber.isEmpty()) {
            binding.cardNumberEditText.error = "Будь ласка, введіть номер картки"
            return false
        }
        if (!cardNumber.all { it.isDigit() }) {
            binding.cardNumberEditText.error = "Номер картки повинен містити лише цифри"
            return false
        }
        if (cardNumber.length !in 13..19) {
            binding.cardNumberEditText.error = "Номер картки повинен містити від 13 до 19 цифр"
            return false
        }
        if (!luhnCheck(cardNumber)) {
            binding.cardNumberEditText.error = "Невірний номер картки"
            return false
        }

        // Перевірка терміну дії: формат MM/YY, карта не прострочена
        val expiryRegex = Regex("^(0[1-9]|1[0-2])/([0-9]{2})\$")
        val matchResult = expiryRegex.find(expiryDate)
        if (matchResult == null) {
            binding.expiryDateEditText.error = "Введіть термін дії у форматі MM/YY"
            return false
        } else {
            val (monthStr, yearStr) = matchResult.destructured
            val month = monthStr.toInt()
            val year = yearStr.toInt() + 2000  // припускаємо, що карта дійсна до 2099 року
            val calendar = java.util.Calendar.getInstance()
            val currentYear = calendar.get(java.util.Calendar.YEAR)
            val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1  // Calendar.MONTH починається з 0
            if (year < currentYear || (year == currentYear && month < currentMonth)) {
                binding.expiryDateEditText.error = "Карта прострочена"
                return false
            }
        }

        // Перевірка CVV: не порожній, складається лише з цифр, довжина 3 або 4
        if (cvv.isEmpty()) {
            binding.cvvEditText.error = "Будь ласка, введіть CVV"
            return false
        }
        if (!cvv.all { it.isDigit() }) {
            binding.cvvEditText.error = "CVV повинен містити лише цифри"
            return false
        }
        if (cvv.length != 3 && cvv.length != 4) {
            binding.cvvEditText.error = "CVV повинен містити 3 або 4 цифри"
            return false
        }

        return true
    }

    private fun luhnCheck(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].digitToInt()
            if (alternate) {
                n *= 2
                if (n > 9) n -= 9
            }
            sum += n
            alternate = !alternate
        }
        return (sum % 10 == 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(cartItems: ArrayList<Pair<CartProduct, Product>>): PaymentFragment {
            val fragment = PaymentFragment()
            val args = Bundle().apply {
                putSerializable("cartItems", cartItems)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
