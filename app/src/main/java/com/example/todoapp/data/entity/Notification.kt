package com.example.todoapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val notiId: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: Int, // 1: Statistic (Biểu đồ), 2: Completed (Tích xanh), 3: Reminder (Cảnh báo)
    val isRead: Boolean = false // Để tô màu nền (nếu cần)
)
