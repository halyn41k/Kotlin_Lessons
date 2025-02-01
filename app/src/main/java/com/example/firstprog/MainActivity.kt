package com.example.firstprog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}
