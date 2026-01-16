package com.example.todoapp.data.repository

import com.example.todoapp.R
import com.example.todoapp.data.dao.CategoryDao
import com.example.todoapp.data.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

    suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getCategoryById(id)
    }

    suspend fun initDefaultCategories(userId: Int) {
        val defaultList = listOf(
            Category(name = "Work", icon = R.drawable.ic_work, colorCode = R.color.primary_blue, userId = userId),
            Category(name = "Personal", icon = R.drawable.ic_person, colorCode = R.color.purple_200, userId = userId),
            Category(name = "Shopping", icon = R.drawable.ic_shopping, colorCode = R.color.white, userId = userId),
            Category(name = "Fitness", icon = R.drawable.ic_fitness, colorCode = R.color.card_red, userId = userId)
        )
        categoryDao.insertCategories(defaultList)
    }
}