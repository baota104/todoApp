package com.example.todoapp.ui.home.profile.changepass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.utils.Security
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _changeResult = MutableLiveData<Result<String>>()
    val changeResult: LiveData<Result<String>> get() = _changeResult

    fun changePassword(userId: Int, currentPass: String, newPass: String, confirmPass: String) {
        if (newPass != confirmPass) {
            _changeResult.value = Result.failure(Exception("Confirm password does not match"))
            return
        }

        if (newPass.length < 6) {
            _changeResult.value = Result.failure(Exception("New password must be at least 6 characters"))
            return
        }

        viewModelScope.launch {
            val user = userRepository.getUserById(userId)


            if (user != null) {
                val storedSalt = user.salt

                val inputHash = Security.hashPassword(newPass, storedSalt)
                val currentHash = Security.hashPassword(currentPass, storedSalt)


                if (user.password == currentHash) {
                    val updatedUser = user.copy(password = inputHash)
                    userRepository.updateUser(updatedUser)
                    _changeResult.postValue(Result.success("Password updated successfully"))
                } else {
                    _changeResult.postValue(Result.failure(Exception("Incorrect current password")))
                }
            } else {
                _changeResult.postValue(Result.failure(Exception("User not found")))
            }
        }
    }

    class Factory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
                return ChangePasswordViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}