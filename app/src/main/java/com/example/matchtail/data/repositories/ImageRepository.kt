package com.example.matchtail.data.repositories

import android.net.Uri
import com.example.matchtail.App
import com.example.matchtail.data.local.AppLocalDB
import com.example.matchtail.data.models.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageRepository(private val folder: String) {

    private val context = App.context

    /**
     * Saves the image from the given Uri to a local file in the app's internal cache.
     * Only the local file path is stored in the database.
     */
    suspend fun upload(imageUri: Uri, imageId: String): String = withContext(Dispatchers.IO) {
        val fileName = "$imageId.jpg"
        val dir = File(context.cacheDir, folder)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, fileName)

        try {
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            val localPath = file.absolutePath
            AppLocalDB.getInstance().imageDao().insertAll(Image(imageId, localPath))
            localPath
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Retrieves the local image path. Implements lazy loading by checking 
     * the filesystem if the database record is missing.
     */
    suspend fun getImagePathById(imageId: String): String = withContext(Dispatchers.IO) {
        // 1. Try to get from Database first using synchronous (suspend) call
        val image = AppLocalDB.getInstance().imageDao().getByIdSync(imageId)
        
        if (image != null && File(image.uri).exists()) {
            return@withContext image.uri
        }

        // 2. Lazy loading: Check if the file exists in the folder even if not in DB
        val fileName = "$imageId.jpg"
        val file = File(File(context.cacheDir, folder), fileName)
        if (file.exists()) {
            val localPath = file.absolutePath
            AppLocalDB.getInstance().imageDao().insertAll(Image(imageId, localPath))
            return@withContext localPath
        }

        ""
    }

    /**
     * Deletes the local file and its database record.
     */
    suspend fun delete(imageId: String) = withContext(Dispatchers.IO) {
        val path = getImagePathById(imageId)
        if (path.isNotEmpty()) {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }
        AppLocalDB.getInstance().imageDao().delete(imageId)
    }

    /**
     * Removes the local database entry.
     */
    suspend fun deleteLocal(imageId: String) = withContext(Dispatchers.IO) {
        AppLocalDB.getInstance().imageDao().delete(imageId)
    }

    // Stub methods for compatibility with code that previously used remote storage
    suspend fun getImageRemoteUri(imageId: String): Uri = Uri.EMPTY
    fun downloadAndCacheImage(uri: Uri, imageId: String): String = uri.path ?: ""
}
