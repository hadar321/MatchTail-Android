package com.example.matchtail.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface ImageLoader {
    suspend fun getImagePath(imageId: String): String
}

abstract class ImageLoaderViewModel : ViewModel() {
    fun getImageUrl(imageId: String, imageLoader: ImageLoader, onCompleted: (imageUrl: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageUrl = imageLoader.getImagePath(imageId)
            withContext(Dispatchers.Main) { onCompleted(imageUrl) }
        }
    }
}