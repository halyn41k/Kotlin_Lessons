package com.example.newfragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment() {
    private lateinit var session: SessionManager
    private lateinit var etName: EditText
    private lateinit var tvDob: TextView
    private lateinit var etAbout: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())

        etName = view.findViewById(R.id.etName)
        tvDob = view.findViewById(R.id.tvDob)
        etAbout = view.findViewById(R.id.etAbout)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnRegister = view.findViewById(R.id.btnRegister)

        tvDob.setOnClickListener {
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    calendar.set(year, month, dayOfMonth)
                    tvDob.text = sdf.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }

        btnRegister.setOnClickListener {
            validateAndRegister()
        }
    }

    private fun validateAndRegister() {
        val name = etName.text.toString().trim()
        val dob = tvDob.text.toString().trim()
        val about = etAbout.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "Введіть ім’я"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Некоректний email"
            return
        }
        if (password.length < 6) {
            etPassword.error = "Пароль має бути не менше 6 символів"
            return
        }

        // ✅ Зберігаємо всі дані у SessionManager
        session.saveUser(name, dob, about, email, password)

        Toast.makeText(requireContext(), "Реєстрація успішна!", Toast.LENGTH_SHORT).show()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
    }
}
