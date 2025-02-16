package com.example.newfragment

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream

class AboutFragment : Fragment() {
    private lateinit var avatar: ImageView
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAbout: EditText
    private lateinit var etDob: EditText
    private lateinit var tvUserInfo: TextView
    private lateinit var sessionManager: SessionManager

    // Реєструємо ActivityResult для вибору зображення
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Встановлюємо зображення в ImageView
                avatar.setImageBitmap(bitmap)

                // Зберігаємо зображення у внутрішню пам'ять
                val avatarFile = File(requireContext().filesDir, "avatar.jpg")
                val outputStream = FileOutputStream(avatarFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sessionManager = SessionManager(requireContext()) // Ініціалізація SessionManager

        avatar = view.findViewById(R.id.avatar)
        tvUserInfo = view.findViewById(R.id.tv_user_info)
        etName = view.findViewById(R.id.et_name)
        etEmail = view.findViewById(R.id.et_email)
        etAbout = view.findViewById(R.id.et_about)
        etDob = view.findViewById(R.id.et_dob)

        val btnChangeAvatar = view.findViewById<Button>(R.id.btn_change_avatar)
        val btnSave = view.findViewById<Button>(R.id.btn_save_profile)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete_account)

        loadUserData() // Завантажуємо дані користувача

        btnChangeAvatar.setOnClickListener { pickImageLauncher.launch("image/*") }
        btnSave.setOnClickListener { saveUserData() }
        btnLogout.setOnClickListener { logOut() }
        btnDelete.setOnClickListener { deleteAccount() }

        return view
    }

    // Завантажуємо дані користувача з SessionManager
    private fun loadUserData() {
        val user = sessionManager.getUser()

        val name = user["name"] ?: ""
        val email = user["email"] ?: ""
        val about = user["about"] ?: ""
        val dob = user["dob"] ?: ""

        // Логуємо дані
        Log.d("SharedPref", "Ім'я: $name, Email: $email, Про себе: $about, Дата нар.: $dob")

        // Виводимо дані у TextView
        val userInfoText = "Ім'я: $name\nEmail: $email\nПро себе: $about\nДата нар.: $dob"
        tvUserInfo.text = userInfoText

        // Заповнюємо поля для редагування
        etName.setText(name)
        etEmail.setText(email)
        etAbout.setText(about)
        etDob.setText(dob)

        // Завантаження фото, якщо є
        val avatarFile = File(requireContext().filesDir, "avatar.jpg")
        if (avatarFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(avatarFile.absolutePath)
            avatar.setImageBitmap(bitmap)
        }
    }

    // Зберігаємо дані користувача у SessionManager
    private fun saveUserData() {
        val name = etName.text.toString()
        val email = etEmail.text.toString()
        val about = etAbout.text.toString()
        val dob = etDob.text.toString()

        // Логуємо дані
        Log.d("SharedPref", "Збереження: Ім'я: $name, Email: $email, Про себе: $about, Дата нар.: $dob")

        // Зберігаємо дані у SessionManager
        sessionManager.saveUser(name, dob, about, email, "")

        Toast.makeText(requireContext(), "Дані збережено", Toast.LENGTH_SHORT).show()
        loadUserData() // Оновлюємо дані на екрані
    }

    // Вихід з акаунта
    private fun logOut() {
        sessionManager.logout()

        // Переходимо на LoginFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .addToBackStack(null)
            .commit()
    }

    // Видалення акаунта
    private fun deleteAccount() {
        AlertDialog.Builder(requireContext())
            .setTitle("Видалення акаунту")
            .setMessage("Ви точно хочете видалити акаунт?")
            .setPositiveButton("Так") { _, _ ->
                sessionManager.logout()
                File(requireContext().filesDir, "avatar.jpg").delete()

                requireActivity().finish()
                startActivity(Intent(requireContext(), LoginFragment::class.java))
            }
            .setNegativeButton("Ні", null)
            .show()
    }
}
