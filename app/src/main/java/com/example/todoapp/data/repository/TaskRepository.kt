package com.example.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.dao.SubTaskDao
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.TaskPopulated
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao
) {

    fun getTaskDetail(taskId: Int) = taskDao.getTaskDetail(taskId)

    suspend fun InsertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }
    suspend fun DeleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun updateSubTaskStatus(subTask: SubTask, isDone: Boolean) {
        subTaskDao.updateSubTask(subTask.copy(isDone = isDone))
    }
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    fun getTasksPopulated(userId: Int): LiveData<List<TaskPopulated>>{
        return taskDao.getTasksPopulated(userId)
    }
    suspend fun completeTaskAndSubTasks(taskId: Int) {
        taskDao.completeTaskAndSubTasks(taskId)
    }

    suspend fun uncompleteTaskAndSubTasks(taskId: Int) {
        taskDao.uncompleteTaskAndSubTasks(taskId)
    }

    suspend fun updateTaskFully(task: Task, subTasks: List<SubTask>) {
        taskDao.updateTaskWithSubTasks(task, subTasks)
    }
}