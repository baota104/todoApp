package com.example.todoapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // KHỞI TẠO VIEWMODEL (Dùng Factory để tiêm Repository vào)
    private val registerViewModel: RegisterViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = UserRepository(database.userDao())
        RegisterViewModel.RegisterViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Lắng nghe kết quả Đăng ký từ ViewModel
        registerViewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            val isSuccess = result.first
            val message = result.second

            if (isSuccess) {
                showToast("Đăng ký thành công! Hãy đăng nhập.")
                // Quay lại màn hình Login
                findNavController().popBackStack()
            } else {
                showToast(message) // Hiện lỗi (VD: Email trùng)
            }
        }

        // 2. Nút Back
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 3. Nút Đăng ký
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmailReg.text.toString().trim()
            val pass = binding.etPassReg.text.toString().trim()
            val confirmPass = binding.etConfirmPass.text.toString().trim()

            if (validateRegister(username, email, pass, confirmPass)) {
                registerViewModel.register(username, email, pass)
            }
        }
    }

    private fun validateRegister(user: String, email: String, pass: String, confirm: String): Boolean {
        if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin")
            return false
        }
        if (pass != confirm) {
            showToast("Mật khẩu xác nhận không khớp")
            return false
        }
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}