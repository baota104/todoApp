package com.example.todoapp.ui.auth.register

import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.entity.User
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.ui.home.calendar.addtask.TaskViewModel
import com.example.todoapp.utils.Security
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationResult = MutableLiveData<Pair<Boolean,String>>()
    val registrationResult : LiveData<Pair<Boolean,String>> get() = _registrationResult

    fun register(name:String,email: String,password:String){
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            if(user != null){
                _registrationResult.value = Pair(first = false, second = "Email existed!")
            }
            else{
                val hash = Security.generateSalt()
                val hashedPassword = Security.hashPassword(password,hash)
                val newUser = User(
                    username = name,
                    email = email,
                    password = hashedPassword,
                    salt = hash
                )
                userRepository.registerUser(newUser)
                val checkSave = userRepository.getUserByEmail(email)

                if (checkSave != null) {
                    _registrationResult.value = Pair(true, "Registration Successful!")
                } else {
                    _registrationResult.value = Pair(false, "Database Error: Could not save user")
                }
            }
        }
    }
    class RegisterViewModelFactory(
        private val repository: UserRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                return RegisterViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}