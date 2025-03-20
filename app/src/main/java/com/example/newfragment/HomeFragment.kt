package com.example.newfragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.newfragment.adapter.BannerAdapter
import com.example.newfragment.adapter.Category
import com.example.newfragment.adapter.CategoryAdapter
import com.example.newfragment.adapter.ProductAdapter
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var dots: List<View>
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val imageList = listOf(R.drawable.ban1, R.drawable.ban2, R.drawable.ban3, R.drawable.ban4)

    private lateinit var categoryRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var popularProductsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var popularProductAdapter: ProductAdapter

    private lateinit var newProductsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var newProductAdapter: ProductAdapter

    private lateinit var db: AppDatabase

    private val bannerRunnable = object : Runnable {
        override fun run() {
            if (currentPage == imageList.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
            handler.postDelayed(this, 10000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        db = AppDatabase.getInstance(requireContext())

        // Ініціалізація банера
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
                currentPage = position
            }
        })

        // Ініціалізація RecyclerView для категорій
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val categories = listOf(
            Category("Браслети", R.drawable.braslets),
            Category("Гердани", R.drawable.gerdans),
            Category("Силянки", R.drawable.sylanku),
            Category("Дукати", R.drawable.ducats),
            Category("Сережки", R.drawable.earings),
            Category("Пояси", R.drawable.belts)
        )
        categoryAdapter = CategoryAdapter(categories)
        categoryRecyclerView.adapter = categoryAdapter

        // Ініціалізація RecyclerView для популярних товарів
        popularProductsRecyclerView = view.findViewById(R.id.popularProductsRecyclerView)
        popularProductsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Ініціалізація RecyclerView для новинок
        newProductsRecyclerView = view.findViewById(R.id.newProductsRecyclerView)
        newProductsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Завантаження даних
        loadPopularProducts()
        loadNewProducts()

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

    private fun loadPopularProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            // Припустимо, що цей метод повертає список популярних товарів
            val popularProducts: List<Product> = db.productDao().getPopularProducts()
            withContext(Dispatchers.Main) {
                popularProductAdapter = ProductAdapter(popularProducts)
                popularProductsRecyclerView.adapter = popularProductAdapter
            }
        }
    }

    private fun loadNewProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            // Припустимо, що цей метод повертає список нових товарів
            val newProducts: List<Product> = db.productDao().getNewProducts()
            withContext(Dispatchers.Main) {
                newProductAdapter = ProductAdapter(newProducts)
                newProductsRecyclerView.adapter = newProductAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(bannerRunnable, 1000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(bannerRunnable)
    }
}
