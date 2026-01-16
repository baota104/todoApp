package com.example.todoapp.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)

    companion object {
        // Đổi tên key cho rõ nghĩa là lưu Email
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }

    // 1. Lưu Email (String) thay vì ID
    fun saveUserSession(userid: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_USER_ID, userid) // Dùng putString
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    // 2. Lấy Email ra (Trả về String null nếu chưa có)
    fun getUserId(): Int? {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
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