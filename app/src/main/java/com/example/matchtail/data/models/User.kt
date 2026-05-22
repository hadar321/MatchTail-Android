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
    var role: String = "", // "Adopter" or "Giver"
    var phone: String = "",
    var location: String = "",
    var description: String? = null,
    var lastUpdated: Long? = null
) {
    companion object {
        internal const val ID_KEY = "id"
        internal const val EMAIL_KEY = "email"
        internal const val USERNAME_KEY = "username"
        internal const val IMAGE_URI_KEY = "avatarUrl"
        internal const val ROLE_KEY = "role"
        internal const val PHONE_KEY = "phone"
        internal const val LOCATION_KEY = "location"
        internal const val DESCRIPTION_KEY = "description"
        internal const val TIMESTAMP_KEY = "lastUpdated"

        fun fromJSON(json: Map<String, Any>): User {
            val id = json[ID_KEY] as? String ?: ""
            val email = json[EMAIL_KEY] as? String ?: ""
            val username = json[USERNAME_KEY] as? String ?: ""
            val avatarUrl = json[IMAGE_URI_KEY] as? String ?: ""
            val role = json[ROLE_KEY] as? String ?: ""
            val phone = json[PHONE_KEY] as? String ?: ""
            val location = json[LOCATION_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val timestamp = (json[TIMESTAMP_KEY] as? Timestamp ?: Timestamp(0,0))
            val lastUpdated = timestamp.toDate().time
            return User(id, email, username, avatarUrl, role, phone, location, description, lastUpdated)
        }
    }
}