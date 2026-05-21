package com.example.matchtail.data.models

import androidx.room.DatabaseView

@DatabaseView(
    viewName = "inflatedPosts",
    value = "SELECT posts.*, users.username AS userName, users.avatarUrl AS avatarUrl FROM posts " +
            "INNER JOIN users ON posts.userId = users.id " +
            "ORDER BY posts.lastUpdated DESC"
)
data class InflatedPost(
    var id: String = "",
    var userId: String = "",
    var userName: String? = null,
    var animalId: String = "",
    var content: String = "",
    var animalPictureUrl: String = "",
    var avatarUrl: String? = null,
    var isAdopt: Boolean = false,
    var lastUpdated: Long? = null
)