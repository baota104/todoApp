package com.example.todoapp.utils

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object Security {
    // Tạo chuỗi muối ngẫu nhiên
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    // Mã hóa password + salt bằng thuật toán SHA-256
    fun hashPassword(password: String, salt: String): String {
        val combined = password + salt
        val bytes = MessageDigest.getInstance("SHA-256").digest(combined.toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}