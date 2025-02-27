package com.example.newfragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.User
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etFirstName = view.findViewById(R.id.etName)
        etLastName = view.findViewById(R.id.etAbout)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnRegister = view.findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener { validateAndRegister() }
    }

    private fun validateAndRegister() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(context, "Введіть ім’я та прізвище", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Некоректний email", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(context, "Пароль має бути не менше 6 символів", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            role = "client"
        )

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val userDao = db.userDao()

            val existingUser = userDao.getUserByEmail(email)
            if (existingUser == null) {
                userDao.insertUser(user)
                Toast.makeText(context, "Реєстрація успішна!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .commit()
            } else {
                Toast.makeText(context, "Користувач вже існує!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
