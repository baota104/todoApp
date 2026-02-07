package com.example.todoapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.todoapp.data.entity.Notification
@Dao
interface NotificationDao {
    @Insert
    suspend fun InsertNotification(notification: Notification)


    @Delete
    suspend fun deleteNotification(notification: Notification)


    @Query ("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): LiveData<List<Notification>>


}