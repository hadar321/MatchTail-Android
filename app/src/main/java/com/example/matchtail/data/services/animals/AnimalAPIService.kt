package com.example.matchtail.data.services.animals

import retrofit2.http.GET

interface AnimalAPIService {
    @GET("all/")
    suspend fun getAllAnimalList(
    ): AnimalResponse

    companion object {
        private val apiService: AnimalAPIService = create()

        private fun create(): AnimalAPIService {
            return NetworkModule().retrofit.create(AnimalAPIService::class.java)
        }

        suspend fun getAnimalList(): AnimalResponse {
            return apiService.getAllAnimalList()
        }
    }
}