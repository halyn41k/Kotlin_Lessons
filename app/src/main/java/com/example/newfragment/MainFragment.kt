package com.example.newfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Завантажуємо HomeFragment при старті
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_settings -> MysteryFragment()
                R.id.nav_products -> ListFragment()
                R.id.nav_about -> AboutFragment()
                else -> null
            }
            if (fragment != null) {
                loadFragment(fragment)
                true
            } else {
                false
            }
        }

        return view
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.main_fragment_container, fragment)
        }
    }
}
