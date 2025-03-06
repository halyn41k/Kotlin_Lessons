package com.example.newfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
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
        tvUserPhone = view.findViewById(R.id.tvUserPhone) // Додали поле для телефону
        tvOrders = view.findViewById(R.id.tvOrders)
        tvWishlist = view.findViewById(R.id.tvWishlist)

        loadUserData()
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
                    tvUserPhone.text = user.phoneNumber // Відображення номера телефону
                } else {
                    tvUserEmail.text = "Користувач не знайдений"
                    tvUserName.text = ""
                    tvUserPhone.text = ""
                }
            }
        }
    }
}
