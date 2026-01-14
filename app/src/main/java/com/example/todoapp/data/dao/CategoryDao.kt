package com.example.todoapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.todoapp.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert
    suspend fun insertCategory(category: Category): Long

    // Lấy danh mục của riêng user đó
    @Query("SELECT * FROM categories WHERE user_id = :userId")
    fun getCategoriesByUser(userId: Int): Flow<List<Category>>
}