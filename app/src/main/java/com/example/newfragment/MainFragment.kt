package com.example.newfragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newfragment.databinding.FragmentMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>

    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ініціалізація sharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("photoPrefs", Context.MODE_PRIVATE)

        // Ініціалізація ActivityResultLauncher для дозволу на камеру
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePhoto() // Якщо дозволено, робимо фото
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        // Ініціалізація для запуску зйомки
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri?.let {
                    binding.imageView.setImageURI(it)
                    saveImageUri(it.toString())
                }
            } else {
                Toast.makeText(requireContext(), "Фото не зроблено", Toast.LENGTH_SHORT).show()
            }
        }

        // Ініціалізація для вибору фото з галереї
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    binding.imageView.setImageURI(it)
                    saveImageUri(it.toString())
                }
            }
        }

        binding.btnGoToSecond.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_second)
        }

        binding.btnGoToThird.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_third)
        }

        // Вибір фото з галереї
        binding.btnPickPhoto.setOnClickListener {
            pickImageFromGallery()
        }

        // Зйомка фото
        binding.btnTakePhoto.setOnClickListener {
            takePhoto()
        }

        // Завантаження збереженого фото
        loadSavedImage()
    }

    private fun pickImageFromGallery() {
        val pickIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickImageLauncher.launch(pickIntent)
    }

    private fun takePhoto() {
        if (checkCameraPermission()) {
            val photoFile = createImageFile()
            // Зверніть увагу: authority має відповідати тій, що в маніфесті (наприклад, "com.example.newfragment.provider")
            photoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", photoFile)

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION) // Додано прапорець для надання дозволу
            }

            if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
                takePictureLauncher.launch(takePictureIntent)
            } else {
                Toast.makeText(requireContext(), "Камера недоступна", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Запит дозволу на використання камери
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }


    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir!!)
    }

    private fun saveImageUri(uri: String) {
        sharedPreferences.edit().putString("saved_image_uri", uri).apply()
    }

    private fun loadSavedImage() {
        val savedUri = sharedPreferences.getString("saved_image_uri", null)
        savedUri?.let {
            val uri = Uri.parse(it)
            try {
                requireActivity().contentResolver.openInputStream(uri)?.close()
                binding.imageView.setImageURI(uri)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Файл недоступний", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
