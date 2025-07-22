package com.example.storyapp.ui.addStory

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun uploadImage(file: File, description: String) = repository.uploadImage(file, description)
}