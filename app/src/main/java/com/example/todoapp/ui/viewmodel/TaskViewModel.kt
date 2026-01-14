package com.example.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.entity.TaskWithSubTasks
import com.example.todoapp.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // Giả lập userId = 1 cho demo. Thực tế lấy từ SessionManager
    private val currentUserId = 1

    // Flow danh sách task cho Dashboard
    val allTasks: StateFlow<List<Task>> = repository.getTasksByUser(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flow chi tiết task
    private val _selectedTaskDetail = MutableStateFlow<TaskWithSubTasks?>(null)
    val selectedTaskDetail: StateFlow<TaskWithSubTasks?> = _selectedTaskDetail

    fun loadTaskDetail(taskId: Int) {
        viewModelScope.launch {
            repository.getTaskDetail(taskId).collect {
                _selectedTaskDetail.value = it
            }
        }
    }

    fun toggleSubTask(subTask: com.example.todoapp.data.entity.SubTask) {
        viewModelScope.launch {
            repository.updateSubTaskStatus(subTask, !subTask.isDone)
        }
    }
}