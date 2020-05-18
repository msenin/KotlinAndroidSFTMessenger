package ru.sft.kotlin.messenger.client.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sft.kotlin.messenger.client.data.MessengerRepository
import ru.sft.kotlin.messenger.client.data.entity.Message
import ru.sft.kotlin.messenger.client.data.entity.User
import ru.sft.kotlin.messenger.client.util.Result

class ChatViewModel(
    application: Application,
    val chatId: Int,
    val isSystemChat: Boolean
) :
    AndroidViewModel(application) {

    private val repository = MessengerRepository.getInstance(application)

    val messages = repository.allChatMessagesWithMembers(chatId)

    val chat = repository.chatById(chatId)

    val currentUser: LiveData<User?>
        get() = repository.currentUser

    fun updateMessages() = viewModelScope.launch(Dispatchers.IO) {
        repository.updateMessages(chatId)
    }

    fun createMessage(message: Message) = viewModelScope.launch(Dispatchers.IO) {
        repository.createMessage(message)
    }

    fun deleteMessage(message: Message) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteMessage(message)
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val regResult = repository.sendMessage(chatId, text)
            if (regResult !is Result.Success) {
                Log.w("MESSAGE SENDING", "failed")
            }
        }

    }
}

class ChatViewModelFactory(
    private val application: Application,
    private val chatId: Int,
    private val isSystemChat: Boolean
) :
    ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(application, chatId, isSystemChat) as T
    }
}
