package com.example.matchtail.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.matchtail.data.models.InflatedPost

@Dao
interface InflatedPostDAO {
    @Query("SELECT * FROM inflatedPosts")
    fun getAll(): LiveData<List<InflatedPost>>

    @Query("SELECT * FROM inflatedPosts WHERE userId = :id")
    fun getByUserId(id: String): LiveData<List<InflatedPost>>

    @Query("SELECT * FROM inflatedPosts WHERE animalId = :id AND userId = :loggedUserId " +
            "UNION SELECT * FROM inflatedPosts WHERE animalId = :id AND userId != :loggedUserId")
    fun getByAnimalId(id: String, loggedUserId: String): LiveData<List<InflatedPost>>
}