package com.example.newfragment

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(name: String, dob: String, about: String, email: String, password: String) {
        val editor = prefs.edit()
        editor.putString("USER_NAME", name)
        editor.putString("USER_EMAIL", email)
        editor.putString("USER_ABOUT", about)
        editor.putString("USER_DOB", dob)
        editor.putString("USER_PASSWORD", password) // якщо використовується
        editor.apply()
    }


    fun getUser(): HashMap<String, String?> {
        return hashMapOf(
            "name" to prefs.getString("name", null),
            "dob" to prefs.getString("dob", null),
            "about" to prefs.getString("about", null),
            "email" to prefs.getString("email", null),
            "password" to prefs.getString("password", null)
        )
    }

    fun isLoggedIn(): Boolean {
        return prefs.getString("email", null) != null
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}
