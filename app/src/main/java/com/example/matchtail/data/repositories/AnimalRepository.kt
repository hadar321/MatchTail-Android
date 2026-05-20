package com.example.matchtail.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.example.matchtail.App
import com.example.matchtail.data.local.AppLocalDB
import com.example.matchtail.data.models.Animal
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.Date

class AnimalRepository {
    companion object {
        private const val COLLECTION = "animals"
        private const val LAST_UPDATED = "animalsLastUpdated"

        private val animalRepository = AnimalRepository()

        fun getInstance(): AnimalRepository {
            return animalRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun save(animal: Animal, transaction: Transaction) {
        val documentRef = db.collection(COLLECTION).document(animal.id)

        transaction.set(documentRef, animal)
        transaction.update(documentRef, Animal.TIMESTAMP_KEY, FieldValue.serverTimestamp())
    }

    fun save(animalId: String, transaction: Transaction) {
        val documentRef = db.collection(COLLECTION).document(animalId)
        transaction.update(documentRef, Animal.TIMESTAMP_KEY, FieldValue.serverTimestamp())
    }

    fun getByIdLiveData(animalId: String): LiveData<Animal> {
        return AppLocalDB.getInstance().animalDao().getByIdLiveData(animalId)
    }

    suspend fun getById(animalId: String): Animal? {
        var animal = AppLocalDB.getInstance().animalDao().getById(animalId)

        if (animal == null) {
            animal = db.collection(COLLECTION)
                .document(animalId)
                .get()
                .await().let { document ->
                    document.data?.let {
                        Animal.fromJSON(it).apply { id = document.id }
                    }
                }

            if (animal == null) return null

            AppLocalDB.getInstance().animalDao().insertAll(animal)
        }

        return animal
    }

    fun getByIncluding(searchString: String): LiveData<List<Animal>> {
        return AppLocalDB.getInstance().animalDao().getByIncluding(searchString)
    }

    suspend fun delete(animalId: String) {
        val documentRef = db.collection(COLLECTION).document(animalId)

        db.runTransaction { transaction ->
            transaction.delete(documentRef)
        }.await()

        AppLocalDB.getInstance().animalDao().delete(animalId)
        refresh()
    }

    @Synchronized
    fun refresh() {
        var time: Long = getLastUpdate()

        val animals = runBlocking {
            db.collection(COLLECTION)
                .whereGreaterThanOrEqualTo(Animal.TIMESTAMP_KEY, Timestamp(Date(time)))
                .get().await().documents.map { document ->
                    document.data?.let {
                        Animal.fromJSON(it).apply { id = document.id }
                    }
                }
        }

        for (animal in animals) {
            if (animal == null) continue
            AppLocalDB.getInstance().animalDao().insertAll(animal)
            val lastUpdated = animal.lastUpdated
            if (lastUpdated != null && lastUpdated > time) {
                time = lastUpdated
            }
        }

        setLastUpdate(time + 1)
    }

    private fun getLastUpdate(): Long {
        val sharedPef: SharedPreferences =
            App.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
        return sharedPef.getLong(LAST_UPDATED, 0)
    }

    private fun setLastUpdate(time: Long) {
        App.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
            .edit().putLong(LAST_UPDATED, time).apply()
    }
}