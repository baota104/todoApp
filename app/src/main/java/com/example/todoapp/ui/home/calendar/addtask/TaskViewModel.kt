package com.example.todoapp.ui.home.calendar.addtask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData // <-- Dùng cái này
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.dao.SubTaskDao
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.entity.Category
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.SubTaskRepository
import com.example.todoapp.data.repository.TaskRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepo: TaskRepository,
    private val subTaskRepo: SubTaskRepository,
    private val categoryRepository: CategoryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _tempSubTasks = MutableLiveData<List<SubTask>>(emptyList())
    val tempSubTasks: LiveData<List<SubTask>> get() = _tempSubTasks

    val categories: LiveData<List<Category>> = categoryRepository.getCategoriesByUser(userId = userPreferences.getUserId()!!)

    fun addTempSubTask(content: String) {
        val currentList = _tempSubTasks.value ?: emptyList()
        val newSub = SubTask(content = content, taskId = 0)
        _tempSubTasks.value = currentList + newSub
    }

    fun removeTempSubTask(subTask: SubTask) {
        val currentList = _tempSubTasks.value ?: emptyList()
        _tempSubTasks.value = currentList - subTask
    }

    // trang thai gui ra fragment
    private val _taskEvent = Channel<String>()
    val taskEvent = _taskEvent.receiveAsFlow()

    fun createTask(
        title: String,
        desc: String,
        startDate: Long?,
        endDate: Long?,
        categoryId: Int?,
        priority: Int
    ) {
        viewModelScope.launch {
            try {
                val currentUserId = userPreferences.getUserId()
                if (currentUserId == -1) {
                    _taskEvent.send("Error: User not logged in!")
                    return@launch
                }

                val newTask = Task(
                    title = title,
                    description = desc,
                    startDate = startDate,
                    endDate = endDate,
                    catId = categoryId,
                    userId = currentUserId!!,
                    priority = priority,
                    progressPercent = 0
                )


                val newTaskId = taskRepo.InsertTask(newTask).toInt()

                val currentSubTasks = _tempSubTasks.value ?: emptyList()
                if (currentSubTasks.isNotEmpty()) {
                    val subTasksToSave = currentSubTasks.map { it.copy(taskId = newTaskId) }
                    subTaskRepo.insertSubTasks(subTasksToSave)
                }

                _taskEvent.send("Success")
            } catch (e: Exception) {
                _taskEvent.send("Error: ${e.message}")
            }
        }
    }

    class Factory(
        private val taskRepo: TaskRepository,
        private val subTaskRepo: SubTaskRepository,
        private val categoryRepository: CategoryRepository,
        private val userPreferences: UserPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            return TaskViewModel(taskRepo, subTaskRepo,categoryRepository, userPreferences) as T
        }
    }
}