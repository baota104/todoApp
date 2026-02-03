package com.example.todoapp.data.local

import android.content.Context
import android.content.SharedPreferences

// chuyen thanh object

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "key_user_id"
        private const val FIRST_TIME = "key_category"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }
    fun savecategoryfist(userid: Int){
        val editor = sharedPreferences.edit()
        editor.putBoolean(FIRST_TIME,true)
        editor.apply()
    }

    fun saveUserSession(userid: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(KEY_USER_ID, userid)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUserId(): Int? {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun isInsertCate(): Boolean {
        return sharedPreferences.getBoolean(FIRST_TIME, false)
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}