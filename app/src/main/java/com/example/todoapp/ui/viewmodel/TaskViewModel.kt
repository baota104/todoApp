package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.dao.SubTaskDao
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.local.UserPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    // --- LOGIC SUBTASK TẠM THỜI ---
    private val _tempSubTasks = MutableStateFlow<List<SubTask>>(emptyList())
    val tempSubTasks = _tempSubTasks.asStateFlow()

    fun addTempSubTask(content: String) {
        val newSub = SubTask(content = content, taskId = 0)
        _tempSubTasks.value = _tempSubTasks.value + newSub
    }

    fun removeTempSubTask(subTask: SubTask) {
        _tempSubTasks.value = _tempSubTasks.value - subTask
    }

    // --- LOGIC TẠO TASK CHÍNH ---
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
                    priority = priority
                )

                val newTaskId = taskDao.insertTask(newTask).toInt()

                val subTasksToSave = _tempSubTasks.value.map { it.copy(taskId = newTaskId) }
                if (subTasksToSave.isNotEmpty()) {
                    subTaskDao.insertSubTasks(subTasksToSave)
                }
                _taskEvent.send("Create Task Success!")
            } catch (e: Exception) {
                _taskEvent.send("Error: ${e.message}")
            }
        }
    }

    class Factory(private val taskDao: TaskDao, private val subTaskDao: SubTaskDao,private val userPreferences: UserPreferences) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TaskViewModel(taskDao, subTaskDao,userPreferences) as T
        }
    }
}