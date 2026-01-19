package com.example.todoapp.data.dao

import androidx.room.*
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.entity.TaskWithCategory
import com.example.todoapp.data.entity.TaskWithSubTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // Lấy tất cả task của User, sắp xếp theo ngày tạo mới nhất
    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllTasks(userId: Int): Flow<List<Task>>

    // Lấy chi tiết 1 task kèm theo list việc nhỏ (SubTasks)
    @Transaction // Bắt buộc dùng @Transaction khi query Relation
    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    fun getTaskDetail(taskId: Int): Flow<TaskWithSubTasks>

    // Tìm kiếm task theo tên
    @Query("SELECT * FROM tasks WHERE user_id = :userId AND title LIKE '%' || :query || '%'")
    fun searchTasks(userId: Int, query: String): Flow<List<Task>>

    @Query("""
        SELECT tasks.*, categories.icon AS category_icon, categories.color_code AS category_color
        FROM tasks 
        LEFT JOIN categories ON tasks.cat_id = categories.categoryId
        WHERE tasks.user_id = :userId
        ORDER BY tasks.created_at DESC
    """)
    fun getTasksWithCategory(userId: Int): Flow<List<TaskWithCategory>>
}