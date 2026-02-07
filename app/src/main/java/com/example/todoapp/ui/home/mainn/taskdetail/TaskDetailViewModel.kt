package com.example.todoapp.ui.home.mainn.taskdetail

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskDetailViewModel(private val Taskrepository:TaskRepository,
):ViewModel(){
    private val _updateResult = MutableLiveData<Pair<Boolean, String>>()
    val updateResult get() = _updateResult
    private val _taskId = MutableLiveData<Int>()

    val taskDetail: LiveData<TaskPopulated> = _taskId.switchMap { id ->
        Taskrepository.getTaskDetail(id)
    }

    fun setTaskId(id: Int) {
        if (_taskId.value != id) {
            _taskId.value = id
        }
    }
    fun finishtask(taskid:Int) {
        viewModelScope.launch {
            try {
                Taskrepository.completeTaskAndSubTasks(taskid)
                _updateResult.value = Pair(true, "finish task successfully")

            } catch (e: Exception) {
                _updateResult.value = Pair(false, e.message ?: "finish task failed")
            }

        }
    }

    fun updateParentTaskStatus(isCompleted: Boolean) {
        val currentTask = taskDetail.value?.task ?: return

        viewModelScope.launch {
            val updatedTask = currentTask.copy(isCompleted = isCompleted)
            Taskrepository.updateTask(updatedTask)
        }
    }
    fun updateSubTaskStatus(subtask: SubTask, isDone: Boolean){
        viewModelScope.launch {
            try{
                Taskrepository.updateSubTaskStatus(subtask, isDone)
                _updateResult.value = Pair(true, "update successfully")
            }
            catch (e:Exception){
                _updateResult.value = Pair(false, e.message ?: "update failed")
            }
        }


    }

    class Factory(private val Taskrepository: TaskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
                return TaskDetailViewModel(Taskrepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}