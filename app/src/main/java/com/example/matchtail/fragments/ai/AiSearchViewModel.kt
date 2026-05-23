package com.example.matchtail.fragments.ai

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchtail.data.models.InflatedPost
import com.example.matchtail.data.repositories.InflatedPostRepository
import com.example.matchtail.data.services.GeminiService
import kotlinx.coroutines.launch

class AiSearchViewModel : ViewModel() {
    val searchResults = MutableLiveData<List<InflatedPost>>()
    val isLoading = MutableLiveData<Boolean>()

    fun searchPosts(query: String) {
        val allPosts = InflatedPostRepository.getInstance().getAll().value
        Log.d("AiSearchViewModel", "Search initiated for: $query. Posts count: ${allPosts?.size}")
        
        if (allPosts.isNullOrEmpty()) {
            Log.d("AiSearchViewModel", "No posts found to search in.")
            searchResults.value = emptyList()
            return
        }
        
        isLoading.value = true
        viewModelScope.launch {
            val postContext = allPosts.joinToString("\n") { 
                "Post ID: ${it.id}, Content: ${it.content}, Interests: ${it.interests.joinToString()}" 
            }
            
            val prompt = """
                You are a helpful assistant. Here is a list of posts from an adoption app:
                $postContext
                
                Please return only the Post IDs that are relevant to this user query: "$query".
                If no posts are relevant, return nothing. Return only a comma-separated list of IDs.
            """.trimIndent()
            
            Log.d("AiSearchViewModel", "Sending prompt to Gemini")
            val aiResponse = GeminiService.generateResponse(prompt)
            Log.d("AiSearchViewModel", "Gemini response: $aiResponse")
            
            if (!aiResponse.isNullOrBlank()) {
                val foundIds = aiResponse.split(",").map { it.trim() }.toSet()
                searchResults.value = allPosts.filter { it.id in foundIds }
                Log.d("AiSearchViewModel", "Found ${searchResults.value?.size} relevant posts.")
            } else {
                Log.d("AiSearchViewModel", "No relevant posts found.")
                searchResults.value = emptyList()
            }
            isLoading.value = false
        }
    }
}