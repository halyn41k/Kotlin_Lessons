// LoginFragment.kt (оновлений)
package com.example.newfragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.newfragment.data.AppDatabase
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        tvGoToRegister = view.findViewById(R.id.tvGoToRegister)

        // Стилізація тексту (залишається без змін)
        val fullText = "Немає облікового запису? Створіть його тут"
        val spannable = SpannableString(fullText)
        spannable.setSpan(ForegroundColorSpan(Color.BLACK), 0, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#6B1F1F")), 25, fullText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvGoToRegister.text = spannable


        btnLogin.setOnClickListener { loginUser() }
        tvGoToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val userDao = db.userDao()

            val user = userDao.getUserByEmailAndPassword(email, password)

            if (user != null) {
                val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putString("logged_in_user_email", user.email)
                    apply()
                }
                Toast.makeText(context, "Вхід успішний!", Toast.LENGTH_SHORT).show()

                // Перевірка ролі користувача
                if (user.email == "admin@gmail.com" && user.password == "adminadmin") {
                    // Адміністратор - перехід до AdminFragment
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AdminFragment())
                        .commit()
                } else {
                    // Звичайний користувач - перехід до MainFragment
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MainFragment())
                        .commit()
                }
            } else {
                Toast.makeText(context, "Невірний email або пароль!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}