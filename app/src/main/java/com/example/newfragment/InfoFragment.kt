package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class InfoFragment : Fragment() {

    private lateinit var backgroundPattern: ImageView
    private lateinit var ukraineImage: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var cardView: MaterialCardView
    private lateinit var tvDescription: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Надуваємо макет фрагмента
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        // Знаходимо елементи інтерфейсу за їхніми ID
        backgroundPattern = view.findViewById(R.id.background_pattern)
        ukraineImage = view.findViewById(R.id.ukraine_image)
        tvTitle = view.findViewById(R.id.tvTitle)
        cardView = view.findViewById(R.id.cardView)
        tvDescription = view.findViewById(R.id.tvDescription)

        // Встановлюємо текст із ресурсів (strings.xml)
        tvTitle.text = getString(R.string.about_us_title)
        tvDescription.text = getString(R.string.about_us_text)

        //Встановлюємо зображення
        backgroundPattern.setImageResource(R.drawable.aboutuspattern)
        ukraineImage.setImageResource(R.drawable.ukraine)


        // Додаємо анімацію появи
        setAnimation(ukraineImage, 500)
        setAnimation(cardView, 700)

        return view
    }

    // Метод для створення анімації появи (AlphaAnimation)
    private fun setAnimation(viewToAnimate: View, duration: Long) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = duration
        anim.startOffset = 200 //Невелика затримка
        viewToAnimate.startAnimation(anim)
    }
}