package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.dao.CategoryDao
import com.example.todoapp.data.dao.NotificationDao
import com.example.todoapp.data.dao.SubTaskDao
import com.example.todoapp.data.dao.TaskDao
import com.example.todoapp.data.dao.UserDao
import com.example.todoapp.data.entity.Category
import com.example.todoapp.data.entity.Notification
import com.example.todoapp.data.entity.SubTask
import com.example.todoapp.data.entity.Task
import com.example.todoapp.data.entity.User

@Database(
    entities = [User::class, Category::class, Task::class, SubTask::class, Notification::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun notificationDao(): NotificationDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_management_db"
                )
                    .fallbackToDestructiveMigration() //  Cho phép xóa DB cũ đi làm lại nếu lệch version
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}