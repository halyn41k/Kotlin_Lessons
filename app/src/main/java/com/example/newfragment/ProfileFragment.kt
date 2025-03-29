package com.example.newfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.newfragment.data.AppDatabase
import com.example.newfragment.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var ivProfileImage: ImageView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserPhone: TextView
    private lateinit var tvOrders: TextView
    private lateinit var tvWishlist: TextView
    private lateinit var llDeliveryPayment: View
    private lateinit var llLogout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivProfileImage = view.findViewById(R.id.ivProfileImage)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserPhone = view.findViewById(R.id.tvUserPhone)
        tvOrders = view.findViewById(R.id.tvOrders)
        tvWishlist = view.findViewById(R.id.tvWishlist)
        llDeliveryPayment = view.findViewById(R.id.llDeliveryPayment)
        llLogout = view.findViewById(R.id.llLogout)

        loadUserData()

        tvWishlist.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, WishlistFragment())
                addToBackStack(null)
            }
        }

        // Обробка кліку для "Мої замовлення"
        tvOrders.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, OrderListFragment())
                addToBackStack(null)
            }
        }

        // Обробка кліку для "Доставка і оплата"
        llDeliveryPayment.setOnClickListener {
            // Припустимо, є фрагмент DeliveryPaymentFragment для відображення цієї інформації
            parentFragmentManager.commit {
                replace(R.id.main_fragment_container, DeliveryPaymentFragment())
                addToBackStack(null)
            }
        }

        // Обробка кліку для "Вийти з облікового запису"
        llLogout.setOnClickListener {
            // Очистка SharedPreferences (або виконання іншої логіки виходу)
            val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            // Переходимо на LoginFragment
            parentFragmentManager.commit {
                replace(R.id.fragment_container, LoginFragment())
                // Не додаємо у back stack, щоб користувач не міг повернутися
            }
        }
    }

    private fun loadUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(requireContext())
            val userDao = db.userDao()
            val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userEmail = sharedPreferences.getString("logged_in_user_email", null)

            if (userEmail.isNullOrEmpty()) {
                requireActivity().runOnUiThread {
                    tvUserEmail.text = "Неавторизований користувач"
                    tvUserName.text = ""
                    tvUserPhone.text = ""
                }
                return@launch
            }

            val user: User? = userDao.getUserByEmail(userEmail)
            requireActivity().runOnUiThread {
                if (user != null) {
                    tvUserEmail.text = user.email
                    tvUserName.text = "${user.firstName} ${user.lastName}"
                    tvUserPhone.text = user.phoneNumber
                } else {
                    tvUserEmail.text = "Користувач не знайдений"
                    tvUserName.text = ""
                    tvUserPhone.text = ""
                }
            }
        }
    }
}
