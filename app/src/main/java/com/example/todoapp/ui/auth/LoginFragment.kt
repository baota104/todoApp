package com.example.todoapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.AppDatabase
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // KHỞI TẠO VIEWMODEL
    private val loginViewModel: LoginViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = UserRepository(database.userDao())
        LoginViewModel.LoginViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreferences = UserPreferences(requireContext())

        // Quan sát kết quả trả về (Pair<Boolean, String>)
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            val isSuccess = result.first
            val message = result.second

            if (isSuccess) {
                // LOGIC MỚI: Vì đăng nhập thành công, ta lấy luôn email từ ô nhập liệu để lưu
                val emailInput = binding.etEmail.text.toString()
                userPreferences.saveUserSession(emailInput)

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                // Chuyển sang màn Home
                findNavController().navigate(R.id.action_loginFragment_to_dashBoardFragment)
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // 4. Các nút phụ (chưa có chức năng)
        binding.layoutSocial.setOnClickListener {
            showToast("Tính năng Social Login đang phát triển!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}