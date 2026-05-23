package com.example.matchtail.data.services

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

object GeminiService {
    private val apiKey = "AIzaSyD8qu0utlVxa3HIL09RLEavEmuWlkeRS-I"
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    suspend fun generateResponse(prompt: String): String? {
        return try {
            val response = generativeModel.generateContent(prompt)
            response.text
        } catch (e: Exception) {
            // Log the actual error to understand if it's an API Key or model issue
            android.util.Log.e("GeminiService", "Error generating response", e);            null
        }
    }
}
