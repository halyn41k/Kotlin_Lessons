package com.example.newfragment

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // Перевірка сеансу користувача
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val email = sharedPreferences.getString("logged_in_user_email", null)

            if (email != null) {
                // Якщо користувач залогінений, перевіряємо його роль
                if (email == "admin@gmail.com") {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AdminFragment())
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MainFragment())
                        .commit()
                }
            } else {
                // Якщо користувач не залогінений, завантажуємо екран логіну
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .commit()
            }
        }
    }
}
