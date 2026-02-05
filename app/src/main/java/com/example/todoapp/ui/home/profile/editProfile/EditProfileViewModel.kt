package com.example.todoapp.ui.home.profile.editProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.ui.home.profile.ProfileViewModel
import kotlinx.coroutines.launch

class EditProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _result = MutableLiveData<Pair<Boolean, String>>()
    val result: LiveData<Pair<Boolean, String>> get() = _result

    fun getUserbyId(userId: Int) {
        viewModelScope.launch {
            val result = userRepository.getUserById(userId)
            _user.postValue(result)
        }
    }

    fun updateProfile(user: User) {
        try {
            viewModelScope.launch {
                userRepository.updateUser(user)
                _result.value = Pair(true, "Profile updated successfully")
            }
        }
        catch (e: Exception) {
            _result.value = Pair(false, e.message.toString())
        }
    }



    class Factory(private val userRepository: UserRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
                return EditProfileViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}