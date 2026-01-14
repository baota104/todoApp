package com.example.todoapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.data.entity.SubTask
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {
    @Insert
    suspend fun insertSubTasks(subTasks: List<SubTask>) // Insert nhiều cái cùng lúc

    @Update
    suspend fun updateSubTask(subTask: SubTask) // Dùng khi tick chọn hoàn thành

    @Delete
    suspend fun deleteSubTask(subTask: SubTask)

    @Query("SELECT * FROM sub_tasks WHERE task_id = :taskId")
    fun getSubTasks(taskId: Int): Flow<List<SubTask>>
}