package com.example.todoapp.data.entity

import androidx.room.Embedded
import androidx.room.ColumnInfo

data class TaskWithCategory(
    @Embedded val task: Task, // Nhúng toàn bộ cột của bảng Task vào

    @ColumnInfo(name = "category_icon")
    val categoryIcon: Int?, // Lấy thêm cột icon từ bảng Category

    @ColumnInfo(name = "category_color")
    val categoryColor: Int? // Lấy thêm cột màu (nếu cần dùng luôn)
)