package com.example.aichat.presentation.chat

import com.example.aichat.domain.model.Message

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = "",
    val selectedTemperature: Float = 0.7f
)

enum class TemperatureOption(val value: Float, val label: String, val description: String) {
    PRECISE(0f, "0.0", "Точный"),
    BALANCED(0.7f, "0.7", "Баланс"),
    CREATIVE(1.2f, "1.2", "Креативный")
}
