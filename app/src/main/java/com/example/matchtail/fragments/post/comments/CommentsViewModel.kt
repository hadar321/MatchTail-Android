package com.example.matchtail.fragments.post.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.matchtail.data.models.Comment
import com.example.matchtail.data.models.InflatedComment
import com.example.matchtail.data.repositories.CommentRepository
import com.example.matchtail.data.repositories.InflatedCommentRepository
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.utils.ImageLoaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentsViewModel : ImageLoaderViewModel() {
    private val _comments = MutableLiveData<List<InflatedComment>>()
    val comments: LiveData<List<InflatedComment>> = _comments

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var postId: String = ""

    fun setPostId(postId: String) {
        this.postId = postId
        fetchComments()
    }

    fun fetchComments() {
        if (postId.isEmpty()) return
        
        viewModelScope.launch {
            _isLoading.postValue(true)
            InflatedCommentRepository.getInstance().getByPostId(postId).observeForever {
                _comments.postValue(it)
                _isLoading.postValue(false)
            }
        }
    }

    fun addComment(content: String, onSuccess: () -> Unit, onError: () -> Unit) {
        if (content.isBlank()) return

        val userId = UserRepository.getInstance().getLoggedUserId() ?: return
        val comment = Comment(
            userId = userId,
            postId = postId,
            content = content
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                CommentRepository.getInstance().save(comment)
                viewModelScope.launch(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    onError()
                }
            }
        }
    }
}