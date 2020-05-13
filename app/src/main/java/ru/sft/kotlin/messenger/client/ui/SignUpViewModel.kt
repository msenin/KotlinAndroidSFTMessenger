package ru.sft.kotlin.messenger.client.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.sft.kotlin.messenger.client.R
import ru.sft.kotlin.messenger.client.data.MessengerRepository
import ru.sft.kotlin.messenger.client.util.Result

data class SignUpResult(
    val error: Int? = null
)

data class SignUpFormState(
    val userIdError: Int? = null,
    val displayNameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MessengerRepository.getInstance(application)

    private val _state = MutableLiveData<SignUpFormState>()
    val state: LiveData<SignUpFormState> = _state

    private val _result = MutableLiveData<SignUpResult>()
    val result: LiveData<SignUpResult> = _result

    fun signUp(userId: String, displayName: String, password: String) {
        viewModelScope.launch {
            val regResult = repository.register(userId, displayName, password)
            if (regResult is Result.Success) {
                _result.value = SignUpResult(error = null)
            } else {
                _result.value = SignUpResult(error = R.string.signup_failed)
            }
        }
    }

    fun dataChanged(
        userId: String,
        displayName: String,
        password: String,
        confirm_password: String
    ) {
        if (!isUserIdValid(userId)) {
            _state.value =
                SignUpFormState(userIdError = R.string.invalid_user_id)
        } else if (!isPasswordValid(password)) {
            _state.value =
                SignUpFormState(passwordError = R.string.invalid_password)
        } else if (!isDisplayNameValid(displayName)) {
            _state.value = SignUpFormState(displayNameError = R.string.invalid_user_name)
        } else if (password != confirm_password) {
            _state.value = SignUpFormState(passwordError = R.string.passwords_mismatch)
        } else {
            _state.value =
                SignUpFormState(isDataValid = true)
        }
    }

    private fun isUserIdValid(userId: String): Boolean {
        return userId.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank()
    }

    private fun isDisplayNameValid(displayName: String): Boolean {
        return displayName.isNotBlank()
    }
}