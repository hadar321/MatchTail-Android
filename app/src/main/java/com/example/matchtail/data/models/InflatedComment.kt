package com.example.matchtail.data.models

import androidx.room.DatabaseView

@DatabaseView(viewName = "inflatedComments",
    value = "SELECT comments.*, users.username AS userName, users.avatarUrl AS avatarUrl FROM comments " +
            "INNER JOIN users ON comments.userId = users.id " +
            "ORDER BY comments.lastUpdated DESC")
data class InflatedComment(
    var id: String = "",
    var userId: String = "",
    var userName: String? = null,
    var avatarUrl: String? = null,
    var postId: String = "",
    var content: String = "",
    var lastUpdated: Long? = null
)