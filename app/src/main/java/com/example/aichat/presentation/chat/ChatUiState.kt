package com.example.aichat.presentation.chat

import com.example.aichat.domain.model.Message
import com.example.aichat.domain.model.TokenStats
import com.example.aichat.domain.usecase.CompressHistoryUseCase.Companion.DEFAULT_COMPRESSION_THRESHOLD

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = "",
    val tokenStats: TokenStats = TokenStats(),
    val isCompressing: Boolean = false,            // Идёт процесс сжатия
    val compressionEnabled: Boolean = true,        // Включено ли автосжатие
    val compressionThreshold: Int = DEFAULT_COMPRESSION_THRESHOLD             // Порог для сжатия (количество сообщений)
)
