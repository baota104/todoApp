package com.example.todoapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.todoapp.data.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Delete
    suspend fun delete(category: Category)


    @Query("SELECT * FROM categories WHERE user_id = :userId ORDER BY categoryId ASC")
    fun getCategoriesByUser(userId: Int): LiveData<List<Category>>

    @Query("SELECT * FROM categories ORDER BY categoryId ASC")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM categories WHERE categoryId = :id")
    suspend fun getCategoryById(id: Int): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)

    @Query("SELECT COUNT(*) FROM categories WHERE user_id = :userId")
    suspend fun countCategoriesByUser(userId: Int): Int
}