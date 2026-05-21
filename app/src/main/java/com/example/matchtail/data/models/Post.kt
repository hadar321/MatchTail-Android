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
    var isRelevant: Boolean = true,
    var lastUpdated: Long? = null
) {
    companion object {
        private const val ID_KEY = "id"
        internal const val USER_ID_KEY = "userId"
        internal const val ANIMAL_ID_KEY = "animalId"
        private const val CONTENT_KEY = "content"
        internal const val IMAGE_URI_KEY = "animalPictureUrl"
        private const val ADOPTING_KEY = "adopt"
        private const val IS_RELEVANT_KEY = "isRelevant"
        internal const val TIMESTAMP_KEY = "lastUpdated"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: ""
            val userId = json[USER_ID_KEY] as? String ?: ""
            val animalId = json[ANIMAL_ID_KEY] as? String ?: ""
            val content = json[CONTENT_KEY] as? String ?: ""
            val animalPictureUrl = json[IMAGE_URI_KEY] as? String ?: ""
            val isAdopting = json[ADOPTING_KEY] as? Boolean ?: false
            val isRelevant = json[IS_RELEVANT_KEY] as? Boolean ?: true
            val timestamp = (json[TIMESTAMP_KEY] as? Timestamp ?: Timestamp(0,0))
            val lastUpdated = timestamp.toDate().time

            return Post(id, userId, animalId, content, animalPictureUrl, isAdopting, isRelevant, lastUpdated)
        }
    }

    fun toJSON(): Map<String, Any> {
        return hashMapOf(
            ID_KEY to id,
            USER_ID_KEY to userId,
            ANIMAL_ID_KEY to animalId,
            CONTENT_KEY to content,
            IMAGE_URI_KEY to animalPictureUrl,
            ADOPTING_KEY to isAdopt,
            IS_RELEVANT_KEY to isRelevant,
            TIMESTAMP_KEY to Timestamp(java.util.Date(lastUpdated ?: 0))
        )
    }
}