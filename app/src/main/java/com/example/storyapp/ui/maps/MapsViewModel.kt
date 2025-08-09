package com.example.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStoriesWithLocation() = repository.getStoriesWithLocation()
}