package com.example.matchtail.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.matchtail.data.models.Animal

@Dao
interface AnimalDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg restaurant: Animal)

    @Query("SELECT * FROM animals WHERE id = :animalId")
    fun getById(animalId: String): Animal?

    @Query("SELECT * FROM animals WHERE id = :animalId")
    fun getByIdLiveData(animalId: String): LiveData<Animal>

    @Query("SELECT * FROM animals " +
            "WHERE name LIKE '%' || :searchString || '%' " +
            "ORDER BY lastUpdated DESC")
    fun getByIncluding(searchString: String): LiveData<List<Animal>>

    @Query("DELETE FROM animals WHERE id = :animalId")
    fun delete(animalId: String)
}