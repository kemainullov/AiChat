package com.example.aichat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichat.domain.model.Message
import com.example.aichat.domain.usecase.ClearHistoryUseCase
import com.example.aichat.domain.usecase.GetMessagesUseCase
import com.example.aichat.domain.usecase.SaveMessageUseCase
import com.example.aichat.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val saveMessageUseCase: SaveMessageUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            getMessagesUseCase().collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val inputText = _uiState.value.inputText.trim()
        if (inputText.isEmpty() || _uiState.value.isLoading) return

        val userMessage = Message(
            content = inputText,
            isFromUser = true
        )

        _uiState.update {
            it.copy(
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            saveMessageUseCase(userMessage)

            val allMessages = _uiState.value.messages + userMessage
            val result = sendMessageUseCase(allMessages)

            result.fold(
                onSuccess = { aiMessage ->
                    saveMessageUseCase(aiMessage)
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Произошла ошибка"
                        )
                    }
                }
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
