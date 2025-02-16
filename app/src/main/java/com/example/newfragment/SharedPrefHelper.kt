package com.example.newfragment

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun saveUserName(name: String) {
        prefs.edit().putString("USER_NAME", name).apply()
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    fun saveUserAbout(about: String) {
        prefs.edit().putString("USER_ABOUT", about).apply()
    }

    fun saveUserDob(dob: String) {
        prefs.edit().putString("USER_DOB", dob).apply()
    }

    fun getUserName(): String {
        return prefs.getString("USER_NAME", "") ?: ""
    }

    fun getUserEmail(): String {
        return prefs.getString("USER_EMAIL", "") ?: ""
    }

    fun getUserAbout(): String {
        return prefs.getString("USER_ABOUT", "") ?: ""
    }

    fun getUserDob(): String {
        return prefs.getString("USER_DOB", "") ?: ""
    }

    fun clearUserData() {
        prefs.edit().clear().apply()
    }
}
