package com.example.newfragment

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(name: String, dob: String, about: String, email: String, password: String) {
        prefs.edit()
            .putString("name", name)
            .putString("dob", dob)
            .putString("about", about)
            .putString("email", email)
            .putString("password", password)
            .apply()
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
