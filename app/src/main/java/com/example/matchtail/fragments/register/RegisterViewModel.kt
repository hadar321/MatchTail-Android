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
    val avatarUri = MutableLiveData<String?>(null)
    val role = MutableLiveData("") // "Adopter" or "Giver"
    val phone = MutableLiveData("")
    val location = MutableLiveData("")
    val description = MutableLiveData("")

    val isNameValid = MutableLiveData(true)
    val isEmailValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)
    val isConfirmPasswordValid = MutableLiveData(true)
    val isRoleValid = MutableLiveData(true)
    val isPhoneValid = MutableLiveData(true)
    val isLocationValid = MutableLiveData(true)

    var isLoading = false

    val isFormValid: Boolean
        get() = isNameValid.value == true && isEmailValid.value == true
                && isPasswordValid.value == true && isConfirmPasswordValid.value == true
                && isRoleValid.value == true && isPhoneValid.value == true && isLocationValid.value == true

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
                    val emailVal = email.value ?: throw Exception("Email is required")
                    val passwordVal = password.value ?: throw Exception("Password is required")
                    val nameVal = name.value ?: throw Exception("Name is required")
                    val roleVal = role.value ?: throw Exception("Role is required")
                    val phoneVal = phone.value ?: throw Exception("Phone is required")
                    val locationVal = location.value ?: throw Exception("Location is required")
                    val descriptionVal = description.value
                    val avatarUriVal = avatarUri.value

                    UserRepository.getInstance().create(
                        emailVal, passwordVal, nameVal, avatarUriVal,
                        roleVal, phoneVal, locationVal, descriptionVal
                    )
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
        isRoleValid.value = role.value?.isNotEmpty() == true
        isPhoneValid.value = phone.value?.isNotEmpty() == true
        isLocationValid.value = location.value?.isNotEmpty() == true
    }
}
