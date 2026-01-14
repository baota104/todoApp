package com.example.todoapp.data.repository

import com.example.todoapp.data.dao.UserDao
import com.example.todoapp.data.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.registerUser(user)
    }

    suspend fun login(email: String, pass: String): User? {
        return userDao.login(email, pass)
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}