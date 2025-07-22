package com.example.storyapp.data.pref

data class User(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)