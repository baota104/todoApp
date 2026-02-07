package com.example.todoapp.ui.home.profile.editProfile

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentEditProfileBinding
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val editProfileViewModel: EditProfileViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val userRepository = UserRepository(db.userDao())
        EditProfileViewModel.Factory(userRepository)
    }

    private var currentUser: User? = null

    private var newAvatarPath: String? = null

    // mo bo suu tap anh
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // 1. Hiển thị ảnh vừa chọn lên giao diện (Review)
            binding.ivAvatar.setImageURI(uri)

            // 2. Lưu file ảnh vào bộ nhớ riêng của App và lấy đường dẫn
            newAvatarPath = saveImageToInternalStorage(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPrefs = UserPreferences(requireContext())
        val userId = userPrefs.getUserId()

        if (userId != -1) {
            editProfileViewModel.getUserbyId(userId!!)
        } else {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
        setupActions()
        setupObservers()
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnChangeAvatar.setOnClickListener {
            // Mở gallery, chỉ lọc file ảnh
            pickImageLauncher.launch("image/*")
        }

        binding.btnsave.setOnClickListener {
            saveProfile()
        }
    }

    private fun setupObservers() {
        editProfileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                currentUser = user
                bindData(user)
            }
        }

        editProfileViewModel.result.observe(viewLifecycleOwner) { (isSuccess, message) ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (isSuccess) {
                findNavController().popBackStack()
            }
        }
    }

    private fun bindData(user: User) {

        binding.etName.setText(user.fullName ?: user.username)
        binding.etProfession.setText(user.profession ?: "")
        binding.etDob.setText(user.dateOfBirth ?: "")
        binding.etEmail.setText(user.email ?: "")

        if (!user.avatarPath.isNullOrEmpty()) {
            val imgFile = File(user.avatarPath)
            if (imgFile.exists()) {
                binding.ivAvatar.setImageURI(Uri.fromFile(imgFile))
            }
        }
    }

    private fun saveProfile() {
        val originalUser = currentUser
        if (originalUser == null) return

        val newFullName = binding.etName.text.toString().trim()
        val newProfession = binding.etProfession.text.toString().trim()
        val newDob = binding.etDob.text.toString().trim()
        val newEmail = binding.etEmail.text.toString().trim()

        if (newFullName.isEmpty()) {
            binding.etName.error = "Name is required"
            return
        }

        // 2. Quyết định đường dẫn ảnh avatar
        // Nếu user chọn ảnh mới (newAvatarPath != null) -> dùng cái mới
        // Nếu không -> dùng cái cũ (originalUser.avatarPath)
        val finalAvatarPath = newAvatarPath ?: originalUser.avatarPath

        val updatedUser = originalUser.copy(
            fullName = newFullName,
            profession = newProfession,
            dateOfBirth = newDob,
            email = newEmail,
            avatarPath = finalAvatarPath
            // username, password, salt, userId được giữ nguyên từ originalUser
        )

        editProfileViewModel.updateProfile(updatedUser)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format: dd/MM/yyyy
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etDob.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        val context = requireContext()
        return try {
            // Tạo tên file duy nhất dựa trên thời gian
            val fileName = "avatar_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            // Trả về đường dẫn tuyệt đối
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}