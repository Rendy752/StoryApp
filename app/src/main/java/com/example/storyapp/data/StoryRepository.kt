package com.example.storyapp.data

import androidx.lifecycle.liveData
import com.example.storyapp.data.response.FileUploadResponse
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.pref.User
import com.example.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun getStories() = liveData {
        emit(Result.Loading)
        try {
            val user = userPreference.getSession().first()
            val response = apiService.getStories("Bearer ${user.token}")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(parseError(e)))
        } catch (e: Exception) {
            emit(Result.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    fun getDetailStory(id: String) = liveData {
        emit(Result.Loading)
        try {
            val user = userPreference.getSession().first()
            val response = apiService.getDetailStory("Bearer ${user.token}", id)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(parseError(e)))
        } catch (e: Exception) {
            emit(Result.Error("Terjadi kesalahan: ${e.message}"))
        }
    }

    fun register(name: String, email: String, pass: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, pass)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(parseError(e)))
        }
    }

    fun login(email: String, pass: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, pass)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(parseError(e)))
        }
    }

    fun uploadImage(imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val user = userPreference.getSession().first()
            val response = apiService.addNewStory("Bearer ${user.token}", multipartBody, requestBody)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error(parseError(e)))
        }
    }

    suspend fun saveSession(user: User) {
        userPreference.saveSession(user)
    }

    fun getSession() = userPreference.getSession()

    suspend fun logout() {
        userPreference.logout()
    }

    private fun parseError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, FileUploadResponse::class.java)
            errorResponse.message
        } catch (_: Exception) {
            "Terjadi kesalahan (${e.code()})"
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
}