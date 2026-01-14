package com.example.todoapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.utils.Security
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Pair<Boolean, String>>()
    val loginResult: LiveData<Pair<Boolean, String>> get() = _loginResult

    fun login(email: String, passInput: String) {
        viewModelScope.launch {
            // 1. Tìm user theo email
            val user = userRepository.getUserByEmail(email)
            if (user == null) {
                // Không tìm thấy email trong hệ thống
                _loginResult.value = Pair(false, "Invalid Email or Password")
            } else {
                // 2. Lấy salt đã lưu trong DB của user này
                val storedSalt = user.salt
                val storedHash = user.password

                // 3. Mã hóa mật khẩu người dùng vừa nhập với salt cũ
                val inputHash = Security.hashPassword(passInput, storedSalt)

                // 4. So sánh hash vừa tạo với hash trong DB
                if (inputHash == storedHash) {
                    _loginResult.value = Pair(true, "Login Successful!")
                } else {
                    _loginResult.value = Pair(false, "Invalid Email or Password")
                }
            }
        }
    }

    // Factory cho LoginViewModel
    class LoginViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}