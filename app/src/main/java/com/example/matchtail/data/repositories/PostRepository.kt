package com.example.matchtail.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.net.toUri
import com.example.matchtail.App
import com.example.matchtail.data.local.AppLocalDB
import com.example.matchtail.data.models.Post
import com.example.matchtail.utils.ImageLoader
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.concurrent.Executors

class PostRepository : ImageLoader {
    companion object {
        private const val COLLECTION = "posts"
        private const val LAST_UPDATED = "postsLastUpdated"
        private const val IS_RELEVANT_KEY = "isRelevant"
        private const val INTERESTS_KEY = "interests"

        private val postRepository = PostRepository()

        fun getInstance(): PostRepository {
            return postRepository
        }
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val imageRepository = ImageRepository(COLLECTION)
    private val executor = Executors.newSingleThreadExecutor()

    suspend fun save(post: Post) {
        val isOldPost = post.id.isNotEmpty()
        val documentRef = if (isOldPost)
            db.collection(COLLECTION).document(post.id)
        else
            db.collection(COLLECTION).document().also { post.id = it.id }

        db.runTransaction { transaction ->
            transaction.set(documentRef, post.toJSON())
            transaction.update(documentRef, Post.TIMESTAMP_KEY, FieldValue.serverTimestamp())
        }.await()
        
        if (post.animalPictureUrl.startsWith("file:///")) {
            uploadImage(post.animalPictureUrl, post.id)
        }
    }

    private suspend fun uploadImage(imageUri: String, postId: String) {
        imageRepository.upload(imageUri.toUri(), postId)
    }

    suspend fun getById(postId: String): Post? {
        var post = AppLocalDB.getInstance().postDao().getById(postId)

        if (post == null) {
            post = db.collection(COLLECTION)
                .document(postId)
                .get()
                .await().let { document ->
                    document.data?.let {
                        Post.fromJSON(it).apply { id = document.id }
                    }
                }

            if (post != null) {
                post.animalPictureUrl = imageRepository.downloadAndCacheImage(
                    imageRepository.getImageRemoteUri(postId),
                    postId
                )
                AppLocalDB.getInstance().postDao().insertAll(post)
            }
        }

        return post?.apply { animalPictureUrl = imageRepository.getImagePathById(postId) }
    }

    override suspend fun getImagePath(imageId: String): String {
        return imageRepository.getImagePathById(imageId)
    }

    suspend fun getByAnimalIdAndUserId(animalId: String, userId: String): Post? {
        var post =
            AppLocalDB.getInstance().postDao().getByAnimalIdAndUserId(animalId, userId)

        if (post == null) {
            val posts = db.collection(COLLECTION)
                .whereEqualTo(Post.ANIMAL_ID_KEY, animalId)
                .whereEqualTo(Post.USER_ID_KEY, userId)
                .get()
                .await().documents.map { document ->
                    document.data?.let {
                        Post.fromJSON(it).apply { id = document.id }
                    }
                }
            if (posts.size != 1) return null
            post = posts[0]
            if (post == null) return null
            val postId = post.id

            post.animalPictureUrl = imageRepository.downloadAndCacheImage(
                imageRepository.getImageRemoteUri(postId),
                postId
            )

            AppLocalDB.getInstance().postDao().insertAll(post)
        }

        val postId = post.id

        return post.apply { animalPictureUrl = imageRepository.getImagePathById(postId) }
    }

    fun delete(postId: String, onError: () -> Unit) {
        executor.submit {
            runBlocking {
                try {
                    db.collection(COLLECTION).document(postId).delete().await()
                    AppLocalDB.getInstance().postDao().delete(postId)
                    imageRepository.delete(postId)
                } catch (e: Exception) {
                    onError()
                }
            }
        }
    }

    suspend fun updateRelevance(postId: String, isRelevant: Boolean) {
        db.collection(COLLECTION).document(postId).update(IS_RELEVANT_KEY, isRelevant).await()
        val post = getById(postId)
        if (post != null) {
            post.isRelevant = isRelevant
            AppLocalDB.getInstance().postDao().insertAll(post)
        }
    }

    suspend fun toggleInterest(postId: String, userId: String) {
        val post = getById(postId) ?: return
        val interests = post.interests.toMutableList()
        if (interests.contains(userId)) {
            interests.remove(userId)
        } else {
            interests.add(userId)
        }
        
        db.collection(COLLECTION).document(postId).update(INTERESTS_KEY, interests).await()
        post.interests = interests
        AppLocalDB.getInstance().postDao().insertAll(post)
    }

    suspend fun refresh() {
        var time: Long = getLastUpdate()

        val posts = db.collection(COLLECTION)
            .whereGreaterThanOrEqualTo(Post.TIMESTAMP_KEY, Timestamp(Date(time)))
            .get().await().documents.map { document ->
                document.data?.let {
                    Post.fromJSON(it).apply { id = document.id }
                }
            }

        for (post in posts) {
            if (post == null) continue

            imageRepository.deleteLocal(post.id)
            AppLocalDB.getInstance().postDao().insertAll(post)
            val lastUpdated = post.lastUpdated
            if (lastUpdated != null && lastUpdated > time) {
                time = lastUpdated
            }
        }

        setLastUpdate(time + 1)
    }

    private fun getLastUpdate(): Long {
        val sharedPef: SharedPreferences =
            App.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
        return sharedPef.getLong(LAST_UPDATED, 0)
    }

    private fun setLastUpdate(time: Long) {
        App.context.getSharedPreferences("TAG", Context.MODE_PRIVATE)
            .edit().putLong(LAST_UPDATED, time).apply()
    }
}