package com.example.todoapp.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithSubTasks(
    @Embedded val task: Task,

    @Relation(
        parentColumn = "taskId",
        entityColumn = "task_id"
    )
    val subTasks: List<SubTask>
)