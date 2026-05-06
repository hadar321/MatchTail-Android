package com.example.matchtail.data.models

import androidx.room.DatabaseView
import androidx.room.PrimaryKey

@DatabaseView(viewName = "inflatedPosts",
    value = "SELECT posts.*, animals.name AS animalName, users.username AS userName, users.avatarUrl AS avatarUrl FROM posts " +
            "INNER JOIN animals ON posts.animalId = animals.id " +
            "INNER JOIN users ON posts.userId = users.id " +
            "ORDER BY posts.lastUpdated DESC")
data class InflatedPost (
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var userName: String = "",
    var animalId: String = "",
    var animalName: String = "",
    var content: String = "",
    var animalPictureUrl: String = "",
    var avatarUrl: String = "",
    var isAdopt: Boolean = false,
    var lastUpdated: Long? = null
)