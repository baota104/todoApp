package com.example.todoapp.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)

    companion object {
        // Đổi tên key cho rõ nghĩa là lưu Email
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }

    // 1. Lưu Email (String) thay vì ID
    fun saveUserSession(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_EMAIL, email) // Dùng putString
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    // 2. Lấy Email ra (Trả về String null nếu chưa có)
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    // 3. Kiểm tra đã đăng nhập chưa
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // 4. Đăng xuất
    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}