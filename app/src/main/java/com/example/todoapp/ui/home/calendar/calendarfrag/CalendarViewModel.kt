package com.example.todoapp.ui.home.calendar.calendarfrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.TaskRepository

class CalendarViewModel(
    private val taskRepository: TaskRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val taskList: LiveData<List<TaskPopulated>> = taskRepository.getTasksPopulated(userPreferences.getUserId()!!)


    class Factory(private val repository: TaskRepository,private val userPreferences: UserPreferences ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(repository,userPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}