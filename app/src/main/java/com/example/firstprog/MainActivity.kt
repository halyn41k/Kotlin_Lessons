package com.example.firstprog

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var textView: TextView
    private lateinit var buttonToggle: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textViewStatus)
        buttonToggle = findViewById(R.id.buttonToggle)

        // Ініціалізуємо SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Отримуємо збережений стан (за замовчуванням "OFF")
        val isOn = sharedPreferences.getBoolean("isOn", false)
        updateTextView(isOn)

        buttonToggle.setOnClickListener {
            val newState = !sharedPreferences.getBoolean("isOn", false)
            sharedPreferences.edit().putBoolean("isOn", newState).apply()
            updateTextView(newState)
        }
    }

    private fun updateTextView(isOn: Boolean) {
        textView.text = if (isOn) "ON" else "OFF"
    }
        val button: Button = findViewById(R.id.buttonClickMe)

        button.setOnClickListener {
            Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
        }
    } Toast
}
