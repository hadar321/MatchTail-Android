package com.example.matchtail.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.matchtail.data.local.AppLocalDB
import com.example.matchtail.data.models.InflatedComment
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class InflatedCommentRepository {
    companion object {
        private val inflatedCommentRepository = InflatedCommentRepository()

        fun getInstance(): InflatedCommentRepository {
            return inflatedCommentRepository
        }
    }

    private val executor = Executors.newSingleThreadExecutor()
    var pool = ThreadPoolExecutor(
        1, 1, 0,
        TimeUnit.SECONDS, LinkedBlockingQueue(1), ThreadPoolExecutor.DiscardPolicy()
    )
    val isLoading = MutableLiveData(false)

    fun getByPostId(id: String): LiveData<List<InflatedComment>> {
        refresh()
        return AppLocalDB.getInstance().inflatedCommentDao().getByPostId(id)
    }

    fun getIsLoading(): LiveData<Boolean> {
        return isLoading
    }

    fun refresh() {
        pool.execute {
            refreshHandler()
        }
    }

    @Synchronized
    private fun refreshHandler() {
        isLoading.postValue(true)

        val futures = listOf(Callable {
            runBlocking {
                CommentRepository.getInstance().refresh()
            }
        }, Callable {
            runBlocking {
                UserRepository.getInstance().refresh()
            }
        })

        executor.invokeAll(futures)
        isLoading.postValue(false)
    }
}