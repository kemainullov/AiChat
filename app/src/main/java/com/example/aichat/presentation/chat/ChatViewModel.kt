package com.example.aichat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichat.domain.model.Message
import com.example.aichat.domain.model.MessageType
import com.example.aichat.domain.usecase.CompressHistoryUseCase
import com.example.aichat.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val compressHistoryUseCase: CompressHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val inputText = _uiState.value.inputText.trim()
        if (inputText.isEmpty() || _uiState.value.isLoading || _uiState.value.isCompressing) return

        val userMessage = Message(
            content = inputText,
            isFromUser = true,
            type = MessageType.REGULAR
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            val result = sendMessageUseCase(_uiState.value.messages)

            result.fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            messages = it.messages + response.message,
                            isLoading = false,
                            tokenStats = it.tokenStats.addUsage(response.tokenUsage)
                        )
                    }

                    // Проверяем, нужно ли сжать историю
                    checkAndCompress()
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

    /**
     * Проверяет необходимость сжатия и выполняет его
     */
    private fun checkAndCompress() {
        val state = _uiState.value

        if (!state.compressionEnabled) return
        if (state.isCompressing) return

        if (compressHistoryUseCase.shouldCompress(state.messages, state.compressionThreshold)) {
            compressHistory()
        }
    }

    /**
     * Выполняет сжатие истории диалога
     */
    fun compressHistory() {
        val state = _uiState.value
        if (state.isCompressing || state.isLoading) return

        val (toCompress, toKeep) = compressHistoryUseCase.splitMessagesForCompression(
            state.messages,
            keepLast = 2
        )

        if (toCompress.isEmpty()) return

        _uiState.update { it.copy(isCompressing = true) }

        viewModelScope.launch {
            val result = compressHistoryUseCase(toCompress)

            result.fold(
                onSuccess = { response ->
                    _uiState.update {
                        // Новый список: summary + сохранённые сообщения
                        val newMessages = listOf(response.message) + toKeep

                        it.copy(
                            messages = newMessages,
                            isCompressing = false,
                            tokenStats = it.tokenStats
                                .addUsage(response.tokenUsage)
                                .recordCompression(toCompress.size)
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isCompressing = false,
                            error = "Ошибка сжатия: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Включить/выключить автоматическое сжатие
     */
    fun toggleCompression() {
        _uiState.update { it.copy(compressionEnabled = !it.compressionEnabled) }
    }

    /**
     * Изменить порог сжатия
     */
    fun setCompressionThreshold(threshold: Int) {
        if (threshold >= 4) {
            _uiState.update { it.copy(compressionThreshold = threshold) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Очистить историю и статистику
     */
    fun clearChat() {
        _uiState.update { ChatUiState(compressionEnabled = it.compressionEnabled) }
    }
}
