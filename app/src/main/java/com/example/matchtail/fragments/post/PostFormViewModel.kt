package com.example.matchtail.fragments.post

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchtail.data.models.Post
import com.example.matchtail.data.repositories.PostRepository
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.data.services.animals.AnimalAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class PostFormViewModel : ViewModel() {
    val animalNames: MutableLiveData<List<String>> = MutableLiveData(ArrayList())
    val selectedAnimal = MutableLiveData("")
    val content = MutableLiveData("")
    val imageUri = MutableLiveData("")
    var postId: String? = null

    val isContentValid = MutableLiveData(true)
    val isAnimalValid = MutableLiveData(true)
    val isImageUriValid = MutableLiveData(true)
    val isLoading = MutableLiveData(false)
    val isFormValid: Boolean get() = isContentValid.value == true && isImageUriValid.value == true && isAnimalValid.value == true


    fun initForm(postId: String) {
        isLoading.value = true
        this.postId = postId

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val post = PostRepository.getInstance().getById(postId)
                    ?: throw Exception("Post not found")
                val animalList = fetchAnimalList()
                withContext(Dispatchers.Main) {
                    animalNames.value = animalList
                    selectedAnimal.value = post.animalId
                    content.value = post.content
                    imageUri.value = post.animalPictureUrl
                }
            } catch (e: Exception) {
                Log.e("AddNewPost", "Error fetching post", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    private suspend fun fetchAnimalList(): MutableList<String> {
        val animalList = mutableListOf<String>()

        AnimalAPIService.getAnimalList().message?.forEach { (key, values) ->
            if (values.isNotEmpty()) {
                values.forEach { value ->
                    animalList.add("$value $key")
                }
            } else {
                animalList.add(key)
            }
        }
        return animalList
    }

    fun initForm() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val animalList = fetchAnimalList()
                withContext(Dispatchers.Main) {
                    animalNames.value = animalList
                }
            } catch (e: Exception) {
                Log.e("AddNewReview", "Error fetching post", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    fun submit(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()
        
        Log.d("PostFormViewModel", "Submitting form: isFormValid=$isFormValid, content='${content.value}', imageUri='${imageUri.value}', selectedAnimal='${selectedAnimal.value}'")
        Log.d("PostFormViewModel", "Validation Statuses: content=${isContentValid.value}, image=${isImageUriValid.value}, animal=${isAnimalValid.value}")

        if (!isFormValid) {
            Log.e("PostFormViewModel", "Form validation failed")
            onFailure(null)
            return
        }

        try {
            isLoading.value = true
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val post = getPost()
                    PostRepository.getInstance().save(post)

                    withContext(Dispatchers.Main) { onSuccess() }
                } catch (e: Exception) {
                    Log.e("Add Post", "Error adding post", e)
                    withContext(Dispatchers.Main) { onFailure(e) }
                } finally {
                    withContext(Dispatchers.Main) { isLoading.value = false }
                }
            }
        } catch (e: Exception) {
            Log.e("Add Post", "Error adding post", e)
            onFailure(e)
        }
    }

    private fun validateForm() {
        isContentValid.value = content.value?.isNotEmpty() == true
        isImageUriValid.value = imageUri.value?.isNotEmpty() == true
        isAnimalValid.value = selectedAnimal.value?.isNotEmpty() == true
    }

    private fun getPost(): Post {
        val userId =
            UserRepository.getInstance().getLoggedUserId() ?: throw Exception("User not logged in")
        val content = content.value ?: throw Exception("Content is required")
        val imageUri = imageUri.value ?: throw Exception("Image is required")
        val animal = selectedAnimal.value ?: throw Exception("Animal is required")

        return Post(
            id = postId ?: "",
            userId = userId,
            animalId = animal,
            content = content,
            animalPictureUrl = imageUri.let {
                if (!it.startsWith("file:///")) "file://$it" else it
            }
        )
    }
}
