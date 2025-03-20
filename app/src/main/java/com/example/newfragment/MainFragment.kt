package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.newfragment.CartFragment

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val btnCart = view.findViewById<ImageButton>(R.id.btn_cart)

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
            addToBackStack(null) // Додаємо у стек переходів
        }
    }
}
