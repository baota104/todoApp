package com.example.todoapp.ui.home.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.Notification
import com.example.todoapp.data.repository.NotificationRepository
import kotlinx.coroutines.launch

class NotificationViewModel (private val notificationRepository: NotificationRepository) : ViewModel() {

    private var _resultdelete = MutableLiveData<Pair<Boolean, String>>()
    val resultdelete: LiveData<Pair<Boolean, String>> get() = _resultdelete

    val notifications = notificationRepository.getAllNotifications()

    fun deleteNotification(notification: Notification) {
        try {
            viewModelScope.launch {
                notificationRepository.deleteNotification(notification)
                _resultdelete.value = Pair(true, "Notification deleted successfully")
            }
        }
        catch (e: Exception) {
            _resultdelete.value = Pair(false, e.message ?: "Error deleting notification")
        }
    }
    class Factory(private val notificationRepository: NotificationRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NotificationViewModel(notificationRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}