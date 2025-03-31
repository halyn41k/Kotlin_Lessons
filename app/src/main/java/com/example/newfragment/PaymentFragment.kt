package com.example.newfragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope // Імпортуємо lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.CartProduct
import com.example.newfragment.data.OrderProduct
import com.example.newfragment.data.Payment // Переконайтесь, що клас Payment тут вже без поля paymentId
import com.example.newfragment.data.Product
import com.example.newfragment.databinding.FragmentPaymentBinding
import com.example.newfragment.adapter.OrderSummaryAdapter
// import kotlinx.coroutines.CoroutineScope // Можна видалити, якщо використовуємо lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import java.util.Random
import java.util.Calendar

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderSummaryAdapter: OrderSummaryAdapter
    private lateinit var db: AppDatabase

    private var cartItems = ArrayList<Pair<CartProduct, Product>>()
    private val currentUserId = 1 // Замініть на логіку отримання поточного користувача
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
            @Suppress("UNCHECKED_CAST") // Додаємо Suppress для безпечного приведення типів
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
                if (currentText == previousText) return // Не переформатовуємо, якщо текст не змінився
                isFormatting = true
                val digits = currentText.replace("\\s".toRegex(), "")
                val formatted = digits.chunked(4).joinToString(" ")
                binding.cardNumberEditText.setText(formatted)
                binding.cardNumberEditText.setSelection(formatted.length) // Встановлюємо курсор в кінець
                previousText = formatted // Зберігаємо відформатований текст для порівняння
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
                val digits = currentText.replace("/", "")
                val formatted = if (digits.length >= 2) {
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

    private fun generateOrderId(): Int {
        // Генеруємо унікальний ID для замовлення (можна покращити для гарантованої унікальності)
        return Random().nextInt(1_000_000) + System.currentTimeMillis().toInt() / 1000
    }

    private fun calculateAndDisplayTotalPrice() {
        var totalPrice = 0.0
        for ((cartProduct, product) in cartItems) {
            // Додаємо перевірку на null для product, якщо можливо
            totalPrice += product.price * cartProduct.quantity
        }
        val format = NumberFormat.getCurrencyInstance(Locale("uk", "UA"))
        val formattedTotalPrice = format.format(totalPrice)
        binding.totalPriceTextView.text = "До сплати: $formattedTotalPrice"
    }

    // Функція для збереження даних в БД (виконується в IO потоці)
    private suspend fun savePaymentToDatabase(paymentMethod: String, status: String): Boolean {
        return try {
            var totalPrice = 0.0
            for ((cartProduct, product) in cartItems) { totalPrice += product.price * cartProduct.quantity }
            val calendar = Calendar.getInstance()
            val createdAt = calendar.timeInMillis

            // 1. Створіть об'єкт Payment БЕЗ зайвого поля paymentId
            //    Поле 'id' буде згенеровано базою даних автоматично.
            val payment = Payment(
                userId = currentUserId,
                orderId = orderId,
                amount = totalPrice,
                paymentMethod = paymentMethod,
                status = status,
                createdAt = createdAt
                // Більше немає параметра paymentId тут
            )

            // 2. Вставте Payment і збережіть автозгенерований ID бази даних (Payment.id)
            //    Переконайтеся, що ваш PaymentDao->insert повертає Long!
            val insertedPaymentDbId: Long = db.paymentDao().insert(payment)

            if (insertedPaymentDbId > 0) {
                // 3. Використовуйте збережений ID (з поля 'id') при створенні OrderProduct
                for ((cartProduct, product) in cartItems) {
                    val orderProduct = OrderProduct(
                        orderId = orderId,
                        productId = product.id,
                        quantity = cartProduct.quantity, price = product.price,
                        userId = currentUserId,
                        status = payment.status, // Використовуємо статус з об'єкта payment
                        createdAt = createdAt,
                        paymentMethod = payment.paymentMethod, // Використовуємо метод з об'єкта payment
                        // Призначте ID згенерований для Payment полю OrderProduct.paymentId (зовнішній ключ)
                        paymentId = insertedPaymentDbId.toInt() // Конвертуємо Long в Int, якщо OrderProduct.paymentId - Int
                    )
                    db.orderProductDao().insert(orderProduct)
                }

                // 4. Видаліть товари з кошика ЛИШЕ після успішних вставок Payment та OrderProduct
                for ((cartProduct, _) in cartItems) {
                    db.cartProductDao().delete(cartProduct)
                }
                true // Повернути true - операція успішна
            } else {
                // Обробка помилки: не вдалося вставити запис Payment
                Log.e("PaymentFragment", "Не вдалося зберегти запис платежу, ID не позитивний.")
                false // Повернути false - операція не вдалася
            }
        } catch (e: Exception) {
            // Обробка можливих винятків під час операцій з БД
            Log.e("PaymentFragment", "Помилка збереження платежу або товарів замовлення", e)
            false // Повернути false - операція не вдалася через виняток
        }
    }

    // Функція для запуску збереження та навігації
    private fun savePaymentAndNavigate(paymentMethod: String, status: String) {
        // Використовуйте lifecycleScope для прив'язки до життєвого циклу фрагмента
        // Це безпечніше, ніж CoroutineScope(Dispatchers.IO).launch напряму
        viewLifecycleOwner.lifecycleScope.launch {
            var success = false
            var errorMessage: String? = null

            // Виконуємо операції з БД в фоновому потоці (IO)
            withContext(Dispatchers.IO) {
                success = savePaymentToDatabase(paymentMethod, status)
                if (!success) {
                    // Якщо savePaymentToDatabase повернула false, записуємо повідомлення
                    // Можна додати більш специфічне повідомлення, якщо функція повертатиме причину помилки
                    errorMessage = "Не вдалося зберегти дані замовлення в базі даних."
                }
            }

            // Повертаємось на головний потік (Main) для оновлення UI та навігації
            if (success) {
                // Навігація лише у випадку успіху всіх операцій з БД
                try {
                    val orderConfirmationFragment =
                        OrderConfirmationFragment.newInstance(ArrayList(cartItems), orderId)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, orderConfirmationFragment)
                        .addToBackStack(null) // Додаємо в бекстек для можливості повернення
                        .commit()
                } catch (e: IllegalStateException) {
                    Log.e("PaymentFragment", "Помилка навігації після оплати: ${e.message}")
                    Toast.makeText(requireContext(), "Помилка переходу на екран підтвердження.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Показуємо повідомлення про помилку, якщо операції з БД не вдалися
                Toast.makeText(
                    requireContext(),
                    errorMessage ?: "Сталася невідома помилка під час збереження.", // Надаємо текст помилки
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun validateCardDetails(): Boolean {
        val cardNumber = binding.cardNumberEditText.text.toString().trim().replace("\\s".toRegex(), "")
        val expiryDate = binding.expiryDateEditText.text.toString().trim()
        val cvv = binding.cvvEditText.text.toString().trim()

        var isValid = true // Прапорець валідності

        // Очищаємо попередні помилки
        binding.cardNumberEditText.error = null
        binding.expiryDateEditText.error = null
        binding.cvvEditText.error = null

        // Перевірка номера картки
        if (cardNumber.isEmpty()) {
            binding.cardNumberEditText.error = "Будь ласка, введіть номер картки"
            isValid = false
        } else if (!cardNumber.all { it.isDigit() }) {
            binding.cardNumberEditText.error = "Номер картки повинен містити лише цифри"
            isValid = false
        } else if (cardNumber.length !in 13..19) {
            binding.cardNumberEditText.error = "Номер картки повинен містити від 13 до 19 цифр"
            isValid = false
        } else if (!luhnCheck(cardNumber)) {
            binding.cardNumberEditText.error = "Невірний номер картки"
            isValid = false
        }

        // Перевірка терміну дії
        val expiryRegex = Regex("^(0[1-9]|1[0-2])/([0-9]{2})$")
        val matchResult = expiryRegex.find(expiryDate)
        if (expiryDate.isEmpty()){
            binding.expiryDateEditText.error = "Введіть термін дії"
            isValid = false
        } else if (matchResult == null) {
            binding.expiryDateEditText.error = "Введіть термін дії у форматі MM/YY"
            isValid = false
        } else {
            val (monthStr, yearStr) = matchResult.destructured
            try {
                val month = monthStr.toInt()
                val year = yearStr.toInt() + 2000 // Припускаємо 21 століття
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH) + 1 // Місяці в Calendar з 0

                if (year < currentYear || (year == currentYear && month < currentMonth)) {
                    binding.expiryDateEditText.error = "Карта прострочена"
                    isValid = false
                }
            } catch (e: NumberFormatException) {
                binding.expiryDateEditText.error = "Невірний формат дати"
                isValid = false
            }
        }

        // Перевірка CVV
        if (cvv.isEmpty()) {
            binding.cvvEditText.error = "Будь ласка, введіть CVV"
            isValid = false
        } else if (!cvv.all { it.isDigit() }) {
            binding.cvvEditText.error = "CVV повинен містити лише цифри"
            isValid = false
        } else if (cvv.length !in 3..4) {
            binding.cvvEditText.error = "CVV повинен містити 3 або 4 цифри"
            isValid = false
        }

        return isValid
    }

    // Алгоритм Луна для перевірки номера картки
    private fun luhnCheck(cardNumber: String): Boolean {
        if (cardNumber.isBlank() || !cardNumber.all { it.isDigit() }) {
            return false
        }
        var sum = 0
        var alternate = false
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].toString().toInt() // Безпечніше конвертувати
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        return (sum % 10 == 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Важливо для уникнення витоків пам'яті
    }

    companion object {
        // Фабричний метод для створення екземпляра фрагмента з передачею даних
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