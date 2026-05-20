package com.example.matchtail.fragments.post

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.matchtail.data.local.AppLocalDB
import com.example.matchtail.data.repositories.InflatedPostRepository
import com.example.matchtail.utils.ImageLoaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostsListViewModel : ImageLoaderViewModel() {
    val posts = InflatedPostRepository.getInstance().getAll()
    val isLoading = InflatedPostRepository.getInstance().getIsLoading()

    fun fetchPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("AAA", "Before")
            InflatedPostRepository.getInstance().refresh()
            Log.d("AAA", "After")
            Log.d("AAA", "${InflatedPostRepository.getInstance().getAll().value}")
            Log.d("AAA", "${AppLocalDB.getInstance().postDao().getAll().value}")
            Log.d("AAA", "${AppLocalDB.getInstance().userDao().getAll()}")
            Log.d("AAA", "${posts.value}")
        }
    }
}