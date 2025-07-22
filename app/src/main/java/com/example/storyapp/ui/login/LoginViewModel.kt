package com.example.storyapp.ui.login

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.pref.User

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    fun login(email: String, pass: String) = repository.login(email, pass)

    suspend fun saveSession(user: User) {
        repository.saveSession(user)
    }
}