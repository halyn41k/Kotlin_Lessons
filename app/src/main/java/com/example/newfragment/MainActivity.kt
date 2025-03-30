package com.example.newfragment

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val loggedInUserEmail = sharedPreferences.getString("logged_in_user_email", null)

        if (savedInstanceState == null) {
            if (isLoggedIn && !loggedInUserEmail.isNullOrEmpty()) {
                // Користувач вже увійшов, перенаправляємо на відповідний фрагмент
                if (loggedInUserEmail == "admin@gmail.com") {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AdminFragment())
                        .commit()
                } else {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MainFragment())
                        .commit()
                }
            } else {
                // Користувач не увійшов, запускаємо LoginFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .commit()
            }
        }
    }
}