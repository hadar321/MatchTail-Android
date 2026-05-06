package com.example.matchtail.data.models

import androidx.room.DatabaseView
import androidx.room.PrimaryKey

@DatabaseView(viewName = "inflatedComments",
    value = "SELECT comments.*, users.username AS userName, users.avatarUrl AS avatarUrl FROM comments " +
            "INNER JOIN users ON comments.userId = users.id " +
            "ORDER BY comments.lastUpdated DESC")
data class InflatedComment(
    @PrimaryKey
    var id: String = "",
    var userId: String = "",
    var userName: String = "",
    var avatarUrl: String = "",
    var postId: String = "",
    var content: String = "",
    var lastUpdated: Long? = null
)