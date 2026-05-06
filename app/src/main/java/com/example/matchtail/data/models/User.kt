package com.example.matchtail.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "users")
data class User (
    @PrimaryKey var id: String = "",
    val email: String = "",
    val username: String = "",
    var avatarUrl: String? = null,
    var lastUpdated: Long? = null
) {
    companion object {
        private const val ID_KEY = "id"
        private const val EMAIL_KEY = "email"
        private const val USERNAME_KEY = "username"
        internal const val IMAGE_URI_KEY = "avatarUrl"
        internal const val TIMESTAMP_KEY = "lastUpdated"

        fun fromJSON(json: Map<String, Any>): User {
            val id = json[ID_KEY] as? String ?: ""
            val email = json[EMAIL_KEY] as? String ?: ""
            val username = json[USERNAME_KEY] as? String ?: ""
            val avatarUrl = json[IMAGE_URI_KEY] as? String ?: ""
            val timestamp = (json[TIMESTAMP_KEY] as? Timestamp ?: Timestamp(0,0))
            val lastUpdated = timestamp.toDate().time
            return User(id, email, username, avatarUrl, lastUpdated)
        }
    }
}