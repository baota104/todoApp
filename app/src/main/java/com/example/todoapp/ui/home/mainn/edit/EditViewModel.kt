package com.example.todoapp.ui.home.mainn.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.Category
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.entity.TaskPopulated
import com.example.todoapp.data.repository.CategoryRepository
import com.example.todoapp.data.repository.TaskRepository
import kotlinx.coroutines.launch

class EditViewModel (private val repository: TaskRepository,
                    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private var _resultupdate : MutableLiveData<Pair<Boolean,String>> = MutableLiveData()
    val resultupdate : MutableLiveData<Pair<Boolean,String>>
        get() = _resultupdate
    private var _resultdelete : MutableLiveData<Pair<Boolean,String>> = MutableLiveData()
    val resultdelete : MutableLiveData<Pair<Boolean,String>>
        get() = _resultdelete

    private val _taskId = MutableLiveData<Int>()
    fun categories(userid: Int): LiveData<List<Category>> {
        return categoryRepository.getCategoriesByUser(userid)
    }

    val taskDetail: LiveData<TaskPopulated> = _taskId.switchMap { id ->
        repository.getTaskDetail(id)
    }

    fun setTaskId(id: Int) {
        if (_taskId.value != id) {
            _taskId.value = id
        }
    }


    fun saveChanges(currentTask: Task, currentSubTasks: List<SubTask>)  {

        viewModelScope.launch {
            try {
                repository.updateTaskFully(currentTask, currentSubTasks)
                _resultupdate.value = Pair(true, "Update thành công!")
            } catch (e: Exception) {
                _resultupdate.value = Pair(false, "Lỗi: ${e.message}")
            }
        }
    }
    fun deleteTask(task: Task){

            viewModelScope.launch {
                try{
                    repository.DeleteTask(task)
                    resultdelete.value = Pair(true, "delete successfully")
                }
                catch (e:Exception){
                    resultdelete.value = Pair(false, e.message ?: "delete failed")
                }
            }

    }


    class Factory(private val repository: TaskRepository,private val categoryRepository: CategoryRepository): ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EditViewModel(repository,categoryRepository) as T
        }
    }


}