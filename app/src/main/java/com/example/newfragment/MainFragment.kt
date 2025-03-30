package com.example.newfragment

import CartViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private lateinit var cartCounter: TextView
    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val btnCart = view.findViewById<ImageButton>(R.id.btn_cart)
        cartCounter = view.findViewById(R.id.cart_counter)

        // Ініціалізуємо ViewModel, спільний для Activity
        cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        // Спостерігаємо за змінами лічильника
        cartViewModel.cartCount.observe(viewLifecycleOwner) { count ->
            cartCounter.text = count.toString()
            // Якщо лічильник прихований – показуємо його з анімацією
            if (cartCounter.visibility == View.GONE) {
                cartCounter.visibility = View.VISIBLE
                val scaleAnimation = android.view.animation.ScaleAnimation(
                    0f, 1f, 0f, 1f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
                )
                scaleAnimation.duration = 300
                cartCounter.startAnimation(scaleAnimation)
            } else {
                // Легка пульсація при оновленні
                val pulseAnimation = android.view.animation.ScaleAnimation(
                    1f, 1.2f, 1f, 1.2f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                    android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
                )
                pulseAnimation.duration = 150
                pulseAnimation.repeatMode = android.view.animation.Animation.REVERSE
                pulseAnimation.repeatCount = 1
                cartCounter.startAnimation(pulseAnimation)
            }
        }

        // Завантажуємо HomeFragment при старті
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Обробка кліків на нижнє меню
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment? = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_catalog -> CatalogFragment()
                R.id.nav_info -> InfoFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> null
            }
            fragment?.let {
                loadFragment(it)
                true
            } ?: false
        }

        btnCart.setOnClickListener {
            loadFragment(CartFragment()) // Завантажуємо фрагмент кошика
        }

        return view
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.main_fragment_container, fragment)
            addToBackStack(null)
        }
    }
}
