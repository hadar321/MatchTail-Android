package com.example.matchtail.fragments.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.utils.UserValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel : ViewModel() {
    val name = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")
    val avatarUri = MutableLiveData("")

    val isNameValid = MutableLiveData(true)
    val isEmailValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)
    val isConfirmPasswordValid = MutableLiveData(true)
    val isAvatarUriValid = MutableLiveData(true)

    var isLoading = false

    val isFormValid: Boolean
        get() = isNameValid.value == true && isEmailValid.value == true
                && isPasswordValid.value == true && isConfirmPasswordValid.value == true && isAvatarUriValid.value == true

    private val validator = UserValidator()

    fun register(onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        try {
            isLoading = true
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val email = email.value ?: throw Exception("Email is required")
                    val password = password.value ?: throw Exception("Password is required")
                    val name = name.value ?: throw Exception("Name is required")
                    val avatarUri = avatarUri.value ?: throw Exception("Avatar is required")

                    Log.d("BBDDD","BBBDDD")
                    UserRepository.getInstance().create(email, password, name, avatarUri)
                    Log.d("BBDDD","BBBDDDEEEE")
                } catch (e: Exception) {
                    Log.e("Register", "Error registering user", e)
                    withContext(Dispatchers.Main) { onFailure(e) }
                } finally {
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            Log.e("Register", "Error registering user", e)
            onFailure(e)
        }
    }

    private fun validateForm() {
        isNameValid.value = name.value?.isNotEmpty() == true
        isEmailValid.value = validator.validateEmail(email.value)
        isPasswordValid.value = validator.validatePassword(password.value)
        isConfirmPasswordValid.value =
            validator.validateConfirmPassword(password.value, confirmPassword.value)
        isAvatarUriValid.value = avatarUri.value?.isNotEmpty() == true
    }
}
