package com.example.todoapp.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.local.UserPreferences
import com.example.todoapp.data.repository.UserRepository
import com.example.todoapp.utils.Security
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository,
                     private val userPreferences: UserPreferences
) : ViewModel() {
        private val _loginResult = MutableLiveData<Pair<Boolean,String>>()
        val loginResult: LiveData<Pair<Boolean, String>> get() = _loginResult


    fun login(email: String, passInput: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            if (user == null) {

                _loginResult.value = Pair(false, "Invalid Email or Password")
            } else {

                val storedSalt = user.salt
                val storedHash = user.password


                val inputHash = Security.hashPassword(passInput, storedSalt)

                if (inputHash == storedHash) {
                    userPreferences.saveUserSession(user.userId)
                    _loginResult.value = Pair(true, "Login Successful!")
                } else {
                    _loginResult.value = Pair(false, "Invalid Email or Password")
                }
            }
        }
    }

    class LoginViewModelFactory(
        private val repository: UserRepository,
        private val userPreferences: UserPreferences
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(repository, userPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}