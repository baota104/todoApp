package com.example.todoapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["cat_id"],
            onDelete = ForeignKey.SET_NULL // Xóa Category -> Task vẫn còn (nhưng mất nhãn)
        )
    ],
    indices = [Index("user_id"), Index("cat_id")] // Tăng tốc độ truy vấn
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,

    val title: String,
    val description: String? = null,

    @ColumnInfo(name = "start_date")
    val startDate: Long? = null, // Lưu timestamp

    @ColumnInfo(name = "end_date")
    val endDate: Long? = null,   // Lưu timestamp

    val priority: Int = 1,       // 1: Thấp, 2: Cao

    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false,

    // Trường này khớp với thanh trượt trên giao diện Dashboard [cite: 42]
    @ColumnInfo(name = "progress_percent")
    val progressPercent: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    // Khóa ngoại
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "cat_id") val catId: Int? = null
)