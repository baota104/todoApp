package com.example.todoapp.ui.home.mainn

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.TaskRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository,
    private val userPreferences: UserPreferences
    ): ViewModel() {

    val taskList: LiveData<List<TaskPopulated>> = taskRepository.getTasksPopulated(userPreferences.getUserId()!!)

    fun insertDefaultCategories() {
        val hasInsertedDefaultCategories = userPreferences.isInsertCate()

        if (!hasInsertedDefaultCategories) {
            viewModelScope.launch {
                categoryRepository.initDefaultCategories(userId = userPreferences.getUserId()!!)
                userPreferences.savecategoryfist(userPreferences.getUserId()!!)
            }
        }
    }


    class Factory(
        private val categoryRepository: CategoryRepository,
        private val taskRepository: TaskRepository,
        private val userPreferences: UserPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return HomeViewModel(categoryRepository,taskRepository, userPreferences) as T
        }
    }
}