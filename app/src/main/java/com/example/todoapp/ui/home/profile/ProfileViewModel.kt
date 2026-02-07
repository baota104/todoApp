package com.example.todoapp.ui.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {


    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _completedTasksCount = MutableLiveData<Int>()
    val completedTasksCount: LiveData<Int> get() = _completedTasksCount


    fun getUserbyId(userId: Int) {
        viewModelScope.launch {
            val result = userRepository.getUserById(userId)
            _user.postValue(result)
        }
    }
    fun countCompletedTasks(userId: Int) {
        viewModelScope.launch {
          val result = userRepository.getCompletedTasksCount(userId)
            _completedTasksCount.postValue(result)
        }
    }


    class Factory(private val userRepository: UserRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }


}