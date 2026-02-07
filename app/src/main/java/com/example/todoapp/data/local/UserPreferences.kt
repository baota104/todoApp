package com.example.todoapp.data.local

import android.content.Context
import android.content.SharedPreferences

// chuyen thanh object

class UserPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("todo_app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_FIRST_TIME_APP = "key_is_first_time"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_FIRST_TIME_INSERT_CATE = "key_is_first_time_insert_cate"
    }

    // danh dau mo app lan dau
    fun setFirstTimeLaunchComplete(){
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_FIRST_TIME_APP,false)
        editor.apply()
    }

    fun isFirstTimeLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_APP, true)
    }

    fun isFirstTimeInsertCate(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_INSERT_CATE, true)
    }

    fun setFirstTimeInsertCate() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_FIRST_TIME_INSERT_CATE, false)
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

    

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_IS_LOGGED_IN)
        editor.remove(KEY_FIRST_TIME_INSERT_CATE)
        editor.apply()
    }
}