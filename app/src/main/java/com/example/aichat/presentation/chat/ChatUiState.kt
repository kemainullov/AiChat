package com.example.aichat.presentation.chat

import com.example.aichat.domain.model.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = "",
    val systemPrompt: String = "Ты полезный AI-ассистент.",
    val isSystemPromptDialogOpen: Boolean = false
)
