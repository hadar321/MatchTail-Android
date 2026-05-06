package com.example.matchtail.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var postId: String = "",
    var content: String = "",
    var lastUpdated: Long? = null
) {
    companion object {
        private const val ID_KEY = "id"
        private const val USER_ID_KEY = "userId"
        private const val POST_ID_KEY = "postId"
        private const val CONTENT_KEY = "content"
        const val TIMESTAMP_KEY = "lastUpdated"

        fun fromJSON(json: Map<String, Any>): Comment {
            val id = json[ID_KEY] as? String ?: ""
            val userId = json[USER_ID_KEY] as? String ?: ""
            val postId = json[POST_ID_KEY] as? String ?: ""
            val content = json[CONTENT_KEY] as? String ?: ""
            val timestamp = (json[TIMESTAMP_KEY] as? Timestamp ?: Timestamp(0,0))
            val lastUpdated = timestamp.toDate().time
            return Comment(id, userId, postId, content, lastUpdated)
        }
    }
}