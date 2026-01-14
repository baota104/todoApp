package com.example.todoapp.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.utils.Security
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Pair<Boolean, String>: Boolean là thành công hay không, String là thông báo lỗi/thành công
    private val _registrationResult = MutableLiveData<Pair<Boolean, String>>()
    val registrationResult: LiveData<Pair<Boolean, String>> get() = _registrationResult

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            Log.d("AuthDebug", "1. Bắt đầu đăng ký cho email: $email")

            try {
                // Kiểm tra user tồn tại
                val existingUser = userRepository.getUserByEmail(email)
                Log.d("AuthDebug", "2. Kết quả kiểm tra user tồn tại: $existingUser")

                if (existingUser != null) {
                    Log.d("AuthDebug", "3. Lỗi: Email đã tồn tại")
                    _registrationResult.value = Pair(false, "Email already exists!")
                } else {
                    Log.d("AuthDebug", "4. Email chưa tồn tại -> Bắt đầu tạo user mới")

                    val salt = Security.generateSalt()
                    val hashedPassword = Security.hashPassword(pass, salt)

                    val newUser = User(
                        username = name,
                        email = email,
                        password = hashedPassword,
                        salt = salt
                    )

                    // Gọi lệnh lưu
                    userRepository.registerUser(newUser)
                    Log.d("AuthDebug", "5. Đã gọi hàm insert vào Database")

                    // Kiểm tra lại ngay lập tức xem đã lưu được chưa
                    val checkSave = userRepository.getUserByEmail(email)
                    Log.d("AuthDebug", "6. Kiểm tra lại sau khi lưu: $checkSave")

                    if (checkSave != null) {
                        _registrationResult.value = Pair(true, "Registration Successful!")
                    } else {
                        Log.e("AuthDebug", "7. LỖI NGHIÊM TRỌNG: Insert chạy xong nhưng không thấy dữ liệu!")
                        _registrationResult.value = Pair(false, "Database Error: Could not save user")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthDebug", "EXCEPTION: ${e.message}")
                _registrationResult.value = Pair(false, "Error: ${e.message}")
            }
        }
    }

    // Factory để khởi tạo ViewModel với tham số Repository
    class RegisterViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                return RegisterViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}