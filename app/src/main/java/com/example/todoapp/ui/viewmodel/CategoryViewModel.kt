package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.Category
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: CategoryRepository,
    private val userPreferences: UserPreferences

) : ViewModel() {

    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Giữ flow sống thêm 5s khi xoay màn hình
            initialValue = emptyList()
        )

    fun addCategory(name: String, icon: Int, color: Int, userId: Int) {
        viewModelScope.launch {
            val newCat = Category(name = name, icon = icon, colorCode = color, userId = userId)
            repository.insertCategory(newCat)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun createDefaultsIfEmpty() {
        viewModelScope.launch {
            val currentUserId = userPreferences.getUserId()

            if (currentUserId != -1) {
                if (currentUserId != null) {
                    repository.initDefaultCategories(currentUserId)
                }
            }
        }
    }

    class CategoryViewModelFactory(private val repository: CategoryRepository,private val userPreferences: UserPreferences) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                return CategoryViewModel(repository,userPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}