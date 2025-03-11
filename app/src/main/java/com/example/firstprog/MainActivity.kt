package com.example.firstprog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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

Intent
        findViewById<Button>(R.id.second).setOnClickListener {
            goToSecondActivity()
        }

        findViewById<Button>(R.id.backToThird).setOnClickListener {
            goToThirdActivity()
        }

        // Отримуємо передані дані
        val from = intent.getStringExtra("from")
        if (!from.isNullOrEmpty()) {
            Toast.makeText(this, "Came from $from", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun goToSecondActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("from", "MainActivity") // Передаємо джерело переходу
        startActivity(intent)
    }

    private fun goToThirdActivity() {
        val intent = Intent(this, ThirdActivity::class.java)
        intent.putExtra("from", "MainActivity") // Передаємо джерело переходу
        startActivity(intent)
    }
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
