package com.example.newfragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.bumptech.glide.Glide

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        val loaderImageView: ImageView = findViewById(R.id.loaderImageView)

        // Завантаження анімованого WebP за допомогою Glide
        Glide.with(this)
            .load(R.drawable.logo1)
            .into(loaderImageView)


        // Через 2 секунди переходимо до MainActivity
        loaderImageView.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}
