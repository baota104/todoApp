package com.example.todoapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.entity.TaskPopulated

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE start_date > :currentTime AND is_completed = 0")
    suspend fun getFutureTasks(currentTime: Long): List<Task>


    @Transaction
    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    fun getTaskDetail(taskId: Int): LiveData<TaskPopulated>

    @Transaction
    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY created_at DESC")
    fun getTasksPopulated(userId: Int): LiveData<List<TaskPopulated>>

    @Query("UPDATE tasks SET is_completed = :isCompleted WHERE taskId = :taskId")
    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean)

    @Query("UPDATE sub_tasks SET is_done = :isDone WHERE task_id = :taskId")
    suspend fun updateAllSubTasksStatus(taskId: Int, isDone: Boolean)

    @Transaction
    suspend fun completeTaskAndSubTasks(taskId: Int) {
        updateTaskStatus(taskId, true)
        updateAllSubTasksStatus(taskId, true)
    }

    @Transaction
    suspend fun uncompleteTaskAndSubTasks(taskId: Int) {
        updateTaskStatus(taskId, false)
        updateAllSubTasksStatus(taskId, false)
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSubTasks(subTasks: List<SubTask>)

    // Hàm xóa những Subtask không còn nằm trong danh sách mới (những cái user đã xóa trên UI)
    @Query("DELETE FROM sub_tasks WHERE task_id = :taskId AND subId NOT IN (:activeSubTaskIds)")
    suspend fun deleteObsoleteSubTasks(taskId: Int, activeSubTaskIds: List<Int>)

    // Nếu list rỗng (xóa hết sạch subtask) thì dùng hàm này
    @Query("DELETE FROM sub_tasks WHERE task_id = :taskId")
    suspend fun deleteAllSubTasks(taskId: Int)
    @Transaction
    open suspend fun updateTaskWithSubTasks(task: Task, subTasks: List<SubTask>) {
        // 1. Update Task chính
        updateTask(task)

        // 2. Xử lý Subtask
        if (subTasks.isEmpty()) {
            deleteAllSubTasks(task.taskId)
        } else {
            // Lọc lấy các ID của subtask còn tồn tại trên giao diện (ID > 0)
            // Những cái mới thêm thì ID = 0 (tự tăng sau)
            val activeIds = subTasks.map { it.subId }.filter { it > 0 }

            // Xóa các subtask cũ trong DB mà không có trong list active
            if (activeIds.isNotEmpty()) {
                deleteObsoleteSubTasks(task.taskId, activeIds)
            } else {
                // Trường hợp đặc biệt: user xóa hết cái cũ, chỉ thêm toàn cái mới
                deleteAllSubTasks(task.taskId)
            }

            // Insert mới hoặc Update cũ
            // Đảm bảo gán taskId chính xác cho các subtask
            val readySubTasks = subTasks.map { it.copy(taskId = task.taskId) }
            insertOrUpdateSubTasks(readySubTasks)
        }
    }
}
