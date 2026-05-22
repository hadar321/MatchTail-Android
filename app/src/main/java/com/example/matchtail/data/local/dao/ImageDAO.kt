package com.example.matchtail.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.matchtail.data.models.Image

@Dao
interface ImageDAO {
    @Query("SELECT * FROM images WHERE id = :id")
    fun getById(id: String): LiveData<Image>

    @Query("SELECT * FROM images WHERE id = :id")
    suspend fun getByIdSync(id: String): Image?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg images: Image)

    @Query("DELETE FROM images WHERE id = :id")
    suspend fun delete(id: String)
}