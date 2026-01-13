package com.example.aichat.domain.model

data class Message(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    // Структурированные данные из JSON ответа AI (только для ответов AI)
    val mood: String? = null,
    val topics: List<String>? = null
)
