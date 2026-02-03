package com.example.todoapp.ui.home.calendar.bottomcategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.Category
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository,
    private val userID: Int
) : ViewModel() {
    fun addCategory(name: String, icon: Int, color: Int) {
        viewModelScope.launch {
            val newCat = Category(
                name = name,
                icon = icon,
                colorCode = color,
                userId = userID
            )
            repository.insertCategory(newCat)
        }
    }

    // Factory giữ nguyên
    class CategoryViewModelFactory(
        private val repository: CategoryRepository,
        private val userID: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                return CategoryViewModel(repository,userID) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}