package com.example.todoapp.data.entity

import androidx.room.Embedded
import androidx.room.ColumnInfo
import androidx.room.Relation

data class TaskPopulated(

    @Embedded val task: Task,

    @Relation(
        parentColumn = "cat_id",
        entityColumn = "categoryId"
    )
    val category: Category?,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "task_id"
    )
    val subTasks: List<SubTask>

)
