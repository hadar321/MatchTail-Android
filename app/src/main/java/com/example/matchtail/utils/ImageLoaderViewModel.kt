package com.example.matchtail.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class ImageLoaderViewModel : ViewModel() {
    fun getImageUrl(imageId: String, imageLoader: ImageLoader, onCompleted: (imageUrl: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val imageUrl = imageLoader.getImagePath(imageId)
            withContext(Dispatchers.Main) { onCompleted(imageUrl) }
        }
    }
}