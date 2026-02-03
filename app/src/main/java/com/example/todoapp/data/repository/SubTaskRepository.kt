package com.example.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.data.dao.SubTaskDao
import com.example.todoapp.data.entity.SubTask

class SubTaskRepository(private val subTaskDao: SubTaskDao) {
    suspend fun insertSubTasks(subTasks: List<SubTask>){
        return subTaskDao.insertSubTasks(subTasks)
    }
    suspend fun updateSubTask(subTask: SubTask){
        return subTaskDao.updateSubTask(subTask)
    }
    suspend fun deleteSubTask(subTask: SubTask){
        return subTaskDao.deleteSubTask(subTask)
    }
    fun getSubTasks(taskId: Int):LiveData<List<SubTask>>{
        return subTaskDao.getSubTasks(taskId)
    }
}