package com.example.socialmediaapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.notification.Message
import com.example.socialmediaapp.data.firebase.remote.MessageRemoteDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRemoteDatabase: MessageRemoteDatabase
): ViewModel() {

    private val _messagesList = MutableStateFlow(emptyList<Message>())
    val messagesList: MutableStateFlow<List<Message>> = _messagesList

    fun sendMessage(receiverId: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRemoteDatabase.sendMessage(receiverId, content)
        }
    }

    fun fetchMessagesRealtime(receiverId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRemoteDatabase.fetchMessagesRealtime(receiverId) {
                messagesList.value = it
            }
        }
    }


}