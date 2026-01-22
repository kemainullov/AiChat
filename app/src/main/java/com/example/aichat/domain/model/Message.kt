package com.example.aichat.domain.model

/**
 * Тип сообщения в диалоге
 */
enum class MessageType {
    REGULAR,    // Обычное сообщение пользователя или ассистента
    SUMMARY,    // Сжатое изложение предыдущих сообщений
    SYSTEM      // Системное сообщение (инструкции для AI)
}

data class Message(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.REGULAR,
    val compressedMessagesCount: Int = 0  // Сколько сообщений было сжато (для SUMMARY)
)
