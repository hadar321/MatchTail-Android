package com.example.matchtail.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.matchtail.App
import com.example.matchtail.data.local.dao.AnimalDAO
import com.example.matchtail.data.local.dao.CommentDAO
import com.example.matchtail.data.local.dao.ImageDAO
import com.example.matchtail.data.local.dao.InflatedCommentDAO
import com.example.matchtail.data.local.dao.InflatedPostDAO
import com.example.matchtail.data.local.dao.PostDAO
import com.example.matchtail.data.local.dao.UserDAO
import com.example.matchtail.data.models.Animal
import com.example.matchtail.data.models.Comment
import com.example.matchtail.data.models.Image
import com.example.matchtail.data.models.InflatedComment
import com.example.matchtail.data.models.InflatedPost
import com.example.matchtail.data.models.Post
import com.example.matchtail.data.models.User


@Database(
    entities = [User::class, Image::class, Post::class, Animal::class, Comment::class],
    views = [InflatedPost::class, InflatedComment::class],
    version = 11,
    exportSchema = true
)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun imageDao(): ImageDAO
    abstract fun animalDao(): AnimalDAO
    abstract fun postDao(): PostDAO
    abstract fun commentDao(): CommentDAO
    abstract fun inflatedPostDao(): InflatedPostDAO
    abstract fun inflatedCommentDao(): InflatedCommentDAO
}

object AppLocalDB {
    private val database: AppLocalDbRepository by lazy {
        Room.databaseBuilder(
            context = App.context,
            klass = AppLocalDbRepository::class.java,
            name = "dbFileName.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    fun getInstance(): AppLocalDbRepository {
        return database
    }
}