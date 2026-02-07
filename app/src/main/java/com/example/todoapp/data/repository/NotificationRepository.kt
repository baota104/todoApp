package com.example.todoapp.data.repository

import androidx.lifecycle.LiveData
import com.example.todoapp.data.dao.NotificationDao
import com.example.todoapp.data.entity.Notification

class NotificationRepository (private val notificationDao: NotificationDao) {
    suspend fun insertNotification(notification: Notification) {
        notificationDao.InsertNotification(notification)
    }

    suspend fun deleteNotification(notification: Notification) {
        notificationDao.deleteNotification(notification)
    }


    fun getAllNotifications(): LiveData<List<Notification>> {
        return notificationDao.getAllNotifications()
    }

}