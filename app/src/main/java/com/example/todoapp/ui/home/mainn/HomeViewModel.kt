package com.example.todoapp.ui.home.mainn

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.TaskRepository
import com.example.todoapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val categoryRepository: CategoryRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
    ): ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun getUser() {
        viewModelScope.launch {
            val userId = userPreferences.getUserId()
            if (userId != null) {
                val user = userRepository.getUserById(userId)
                _user.value = user!!
            }
        }
    }
    val taskList: LiveData<List<TaskPopulated>> = taskRepository.getTasksPopulated(userPreferences.getUserId()!!)

    fun insertDefaultCategories() {
        val isFirstTime = userPreferences.isFirstTimeInsertCate()

        if (isFirstTime) {
            viewModelScope.launch {
                categoryRepository.initDefaultCategories(userId = userPreferences.getUserId()!!)
                userPreferences.setFirstTimeInsertCate()
            }
        }
    }


    class Factory(
        private val categoryRepository: CategoryRepository,
        private val taskRepository: TaskRepository,
        private val userRepository: UserRepository,
        private val userPreferences: UserPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return HomeViewModel(categoryRepository,taskRepository,userRepository, userPreferences) as T
        }
    }
}