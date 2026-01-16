package com.example.todoapp.data.repository

import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.dao.SubTaskDao
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.entity.SubTask
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val subTaskDao: SubTaskDao
) {
    fun getTasksByUser(userId: Int): Flow<List<Task>> = taskDao.getAllTasks(userId)

    fun getTaskDetail(taskId: Int) = taskDao.getTaskDetail(taskId)

    suspend fun updateSubTaskStatus(subTask: SubTask, isDone: Boolean) {
        subTaskDao.updateSubTask(subTask.copy(isDone = isDone))
    }

    suspend fun updateTaskProgress(task: Task, percent: Int) {
        taskDao.updateTask(task.copy(progressPercent = percent))
    }
}