package com.example.aichat.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aichat.domain.model.Message
import com.example.aichat.domain.speech.RecognitionState
import com.example.aichat.domain.speech.SpeechRecognitionRepository
import com.example.aichat.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val speechRepository: SpeechRecognitionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            speechRepository.recognitionState.collect { state ->
                when (state) {
                    is RecognitionState.Listening -> {
                        _uiState.update { it.copy(isListening = true) }
                    }
                    is RecognitionState.Result -> {
                        _uiState.update { it.copy(inputText = state.text, isListening = false) }
                    }
                    is RecognitionState.Error -> {
                        _uiState.update { it.copy(isListening = false, error = state.message) }
                    }
                    is RecognitionState.Idle -> {
                        _uiState.update { it.copy(isListening = false) }
                    }
                }
            }
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun startVoiceInput() {
        speechRepository.startListening()
    }

    fun stopVoiceInput() {
        speechRepository.stopListening()
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
                messages = it.messages + userMessage,
                inputText = "",
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            val result = sendMessageUseCase(_uiState.value.messages)

            result.fold(
                onSuccess = { aiMessage ->
                    _uiState.update {
                        it.copy(
                            messages = it.messages + aiMessage,
                            isLoading = false
                        )
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

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        speechRepository.destroy()
    }
}
