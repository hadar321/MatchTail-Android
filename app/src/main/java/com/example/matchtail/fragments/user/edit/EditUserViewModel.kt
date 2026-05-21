package com.example.matchtail.fragments.user.edit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.utils.UserValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditUserViewModel : ViewModel() {
    val name = MutableLiveData("")
    val oldPassword = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")
    val avatarUri = MutableLiveData<String?>(null)
    val role = MutableLiveData("")
    val phone = MutableLiveData("")
    val location = MutableLiveData("")
    val description = MutableLiveData("")

    val isNameValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)
    val isConfirmPasswordValid = MutableLiveData(true)
    val isRoleValid = MutableLiveData(true)
    val isPhoneValid = MutableLiveData(true)
    val isLocationValid = MutableLiveData(true)

    val isLoading = MutableLiveData(false)

    val isFormValid: Boolean
        get() = isNameValid.value == true && isPasswordValid.value == true &&
                isConfirmPasswordValid.value == true && isRoleValid.value == true &&
                isPhoneValid.value == true && isLocationValid.value == true

    private val validator = UserValidator()

    init {
        fetchUserDetails()
    }

    private fun fetchUserDetails() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = UserRepository.getInstance().getLoggedUserId()
                    ?: throw Exception("User not logged in")
                val user = UserRepository.getInstance().getById(userId)
                    ?: throw Exception("User not found")

                withContext(Dispatchers.Main) {
                    name.value = user.username
                    avatarUri.value = user.avatarUrl
                    role.value = user.role
                    phone.value = user.phone
                    location.value = user.location
                    description.value = user.description ?: ""
                }
            } catch (e: Exception) {
                Log.e("Edit", "Error fetching user details", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    fun submit(onSuccess: () -> Unit, onFailure: (error: Exception?) -> Unit) {
        validateForm()

        if (!isFormValid) {
            onFailure(null)
            return
        }

        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val oldPwd = oldPassword.value ?: ""
                val pwd = password.value ?: ""
                val nameVal = name.value ?: throw Exception("Name is required")
                val avatarVal = avatarUri.value
                val roleVal = role.value ?: throw Exception("Role is required")
                val phoneVal = phone.value ?: throw Exception("Phone is required")
                val locationVal = location.value ?: throw Exception("Location is required")
                val descVal = description.value

                UserRepository.getInstance().update(
                    oldPwd, pwd, nameVal, avatarVal,
                    roleVal, phoneVal, locationVal, descVal
                )
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.e("Edit", "Error updating user", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            } finally {
                withContext(Dispatchers.Main) { isLoading.value = false }
            }
        }
    }

    private fun validateForm() {
        isNameValid.value = name.value?.isNotEmpty() == true
        isRoleValid.value = role.value?.isNotEmpty() == true
        isPhoneValid.value = phone.value?.isNotEmpty() == true
        isLocationValid.value = location.value?.isNotEmpty() == true

        if (password.value?.isNotEmpty() == true) {
            isPasswordValid.value = validator.validatePassword(password.value)
            isConfirmPasswordValid.value =
                validator.validateConfirmPassword(password.value, confirmPassword.value)
        } else {
            isPasswordValid.value = true
            isConfirmPasswordValid.value = true
        }
    }
}