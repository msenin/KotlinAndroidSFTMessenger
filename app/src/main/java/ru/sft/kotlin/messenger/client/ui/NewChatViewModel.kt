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

data class NewChatResult(
    val error: Int? = null
)

data class NewChatFormState(
    val titleError: Int? = null,
    val isDataValid: Boolean = false
)

class NewChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MessengerRepository.getInstance(application)

    private val _state = MutableLiveData<NewChatFormState>()
    val state: LiveData<NewChatFormState> = _state

    private val _result = MutableLiveData<NewChatResult>()
    val result: LiveData<NewChatResult> = _result

    fun newChat(title: String) {
        viewModelScope.launch {
            val regResult = repository.createChat(title)
            if (regResult is Result.Success) {
                _result.value = NewChatResult(error = null)
            } else {
                _result.value = NewChatResult(error = R.string.newchat_failed)
            }
        }
    }

    fun dataChanged(
        title: String
    ) {
        if (!isTitleValid(title)) {
            _state.value =
                NewChatFormState(titleError = R.string.invalid_title)
        } else {
            _state.value =
                NewChatFormState(isDataValid = true)
        }
    }

    private fun isTitleValid(userId: String): Boolean {
        return userId.isNotBlank()
    }
}