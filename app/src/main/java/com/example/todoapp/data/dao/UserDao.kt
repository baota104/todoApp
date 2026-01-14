package com.example.todoapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todoapp.data.entity.User

@Dao
interface UserDao {
    // 1. Đăng ký: Thêm user mới
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registerUser(user: User): Long

    // 2. Đăng nhập: Tìm user theo email và pass
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    // 3. Kiểm tra trùng email (để ngăn đăng ký trùng)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
}