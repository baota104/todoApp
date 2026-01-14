package com.example.todoapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_tasks",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE // Xóa Task cha -> Xóa hết việc con
        )
    ],
    indices = [Index("task_id")]
)
data class SubTask(
    @PrimaryKey(autoGenerate = true)
    val subId: Int = 0,

    val content: String, // Nội dung việc nhỏ

    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,

    @ColumnInfo(name = "task_id")
    val taskId: Int // Khóa ngoại
)