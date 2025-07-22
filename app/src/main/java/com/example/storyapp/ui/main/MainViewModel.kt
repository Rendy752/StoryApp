package com.example.storyapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getStories() = repository.getStories()

    fun getSession() = repository.getSession()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}