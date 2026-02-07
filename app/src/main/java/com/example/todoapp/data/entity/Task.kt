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
            onDelete = ForeignKey.SET_NULL // xoa cate -> task con nhung mat nhan
        )
    ],
    indices = [Index("user_id"), Index("cat_id")] // tang toc do truy van
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,

    val title: String,
    val description: String? = null,

    @ColumnInfo(name = "start_date")
    val startDate: Long? = null, //  timestamp

    @ColumnInfo(name = "end_date")
    val endDate: Long? = null,   //  timestamp

    val priority: Int = 1,       // 1: Thấp, 2: Cao

    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false,

    @ColumnInfo(name = "progress_percent")
    val progressPercent: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "cat_id") val catId: Int? = null
)