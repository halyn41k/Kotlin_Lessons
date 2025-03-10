package com.example.newfragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.newfragment.adapter.BannerAdapter

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var dots: List<View>
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val imageList = listOf(R.drawable.ban1, R.drawable.ban2, R.drawable.ban3, R.drawable.ban4)

    private val runnable = object : Runnable {
        override fun run() {
            if (currentPage == imageList.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
            handler.postDelayed(this, 10000) // Змінюємо кожні 10 секунд
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewPager = view.findViewById(R.id.viewPager)
        bannerAdapter = BannerAdapter(imageList)
        viewPager.adapter = bannerAdapter

        dots = listOf(
            view.findViewById(R.id.dot1),
            view.findViewById(R.id.dot2),
            view.findViewById(R.id.dot3),
            view.findViewById(R.id.dot4)
        )

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
                currentPage = position //Оновлюємо поточну сторінку
            }
        })

        return view
    }

    private fun updateDots(currentPosition: Int) {
        for (i in dots.indices) {
            dots[i].background = if (i == currentPosition) {
                resources.getDrawable(R.drawable.dot_active, null)
            } else {
                resources.getDrawable(R.drawable.dot_inactive, null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 1000) // Запускаємо при відновленні фрагмента
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable) // Зупиняємо, коли фрагмент неактивний
    }
}