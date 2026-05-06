package com.example.matchtail.data.repositories

import android.net.Uri
import com.bumptech.glide.Glide
import com.example.matchtail.App
import com.example.matchtail.data.local.AppLocalDB
import com.example.matchtail.data.models.Image
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ImageRepository(private val folder: String) {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun upload(imageUri: Uri, imageId: String) {
        val imageRef = storage.reference.child("$folder/$imageId")
        imageRef.putFile(imageUri).await()

        AppLocalDB.getInstance().imageDao().insertAll(Image(imageId, imageUri.toString()))
    }

    suspend fun getImageRemoteUri(imageId: String): Uri {
        val imageRef = storage.reference.child("$folder/$imageId")

        return imageRef.downloadUrl.await()
    }

    fun downloadAndCacheImage(uri: Uri, imageId: String): String {
        val file = Glide.with(App.context)
            .asFile()
            .load(uri)
            .submit()
            .get()

        AppLocalDB.getInstance().imageDao().insertAll(Image(imageId, file.absolutePath))

        return file.absolutePath
    }

    suspend fun getImagePathById(imageId: String): String {
        val image = AppLocalDB.getInstance().imageDao().getById(imageId).value

        if (image != null) return image.uri

        val remoteUri = getImageRemoteUri(imageId)
        val localPath = downloadAndCacheImage(remoteUri, imageId)

        AppLocalDB.getInstance().imageDao().insertAll(Image(imageId, localPath))

        return localPath
    }

    suspend fun delete(imageId: String) {
        val imageRef = storage.reference.child("$folder/$imageId")
        imageRef.delete().await()

        val image = AppLocalDB.getInstance().imageDao().getById(imageId).value
        image?.let {
            val file = Glide.with(App.context)
                .asFile()
                .load(it.uri)
                .submit()
                .get()

            if (file.exists()) {
                file.delete()
            }

            AppLocalDB.getInstance().imageDao().delete(imageId)
        }
    }

    fun deleteLocal(imageId: String) {
        AppLocalDB.getInstance().imageDao().delete(imageId)
    }
}