package com.example.storyapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.response.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    val story: Flow<PagingData<Story>> =
        repository.getStories().cachedIn(viewModelScope)

    fun getSession() = repository.getSession()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}