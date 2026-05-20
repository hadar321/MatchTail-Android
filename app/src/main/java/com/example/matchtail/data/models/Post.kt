package com.example.matchtail.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "posts")
data class Post (
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var animalId: String = "",
    var content: String = "",
    var animalPictureUrl: String = "",
    var isAdopt: Boolean = false,
    var lastUpdated: Long? = null
) {
    companion object {
        private const val ID_KEY = "id"
        internal const val USER_ID_KEY = "userId"
        internal const val ANIMAL_ID_KEY = "animalId"
        private const val CONTENT_KEY = "content"
        internal const val IMAGE_URI_KEY = "animalPictureUrl"
        private const val ADOPTING_KEY = "adopt"
        internal const val TIMESTAMP_KEY = "lastUpdated"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: ""
            val userId = json[USER_ID_KEY] as? String ?: ""
            val animalId = json[ANIMAL_ID_KEY] as? String ?: ""
            val content = json[CONTENT_KEY] as? String ?: ""
            val animalPictureUrl = json[IMAGE_URI_KEY] as? String ?: ""
            val isAdopting = json[ADOPTING_KEY] as? Boolean ?: false
            val timestamp = (json[TIMESTAMP_KEY] as? Timestamp ?: Timestamp(0,0))
            val lastUpdated = timestamp.toDate().time

            return Post(id, userId, animalId, content, animalPictureUrl, isAdopting, lastUpdated)
        }
    }
}