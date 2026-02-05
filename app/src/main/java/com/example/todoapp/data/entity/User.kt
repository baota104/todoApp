package com.example.todoapp.data.entity
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    val username: String,
    val password: String,

    val fullName: String? = null,
    val email: String? = null,


    val profession: String? = null,
    val dateOfBirth: String? = null,
    val location: String? = null,
    val salt: String,
    val avatarPath: String? = null
)