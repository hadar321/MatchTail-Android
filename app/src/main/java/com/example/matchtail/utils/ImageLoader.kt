package com.example.matchtail.utils

interface ImageLoader {
    suspend fun getImagePath(imageId: String): String
}