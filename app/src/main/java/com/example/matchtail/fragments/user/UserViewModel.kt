package com.example.matchtail.fragments.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.matchtail.data.repositories.InflatedPostRepository
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.utils.ImageLoaderViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(private val userId: String) : ImageLoaderViewModel() {
    val posts = InflatedPostRepository.getInstance().getByUserId(userId)
    val username = MutableLiveData("")
    val avatarUrl = MutableLiveData<String?>(null)
    val role = MutableLiveData("")
    val phone = MutableLiveData("")
    val location = MutableLiveData("")
    val description = MutableLiveData("")

    val isLoadingPosts = InflatedPostRepository.getInstance().getIsLoading()
    val isLoadingUser = MutableLiveData(false)

    init {
        fetchUser()
    }

    fun fetchUser() {
        isLoadingUser.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = UserRepository.getInstance().getById(userId)
                    ?: throw Exception("User not found")
                withContext(Dispatchers.Main) {
                    username.value = user.username
                    avatarUrl.value = user.avatarUrl
                    role.value = user.role
                    phone.value = user.phone
                    location.value = user.location
                    description.value = user.description ?: ""
                }
            } catch (e: Exception) {
                Log.e("User Page", "Error fetching user", e)
            } finally {
                withContext(Dispatchers.Main) { isLoadingUser.value = false }
            }
        }
    }

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            InflatedPostRepository.getInstance().refresh()
        }
    }
}