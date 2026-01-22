package com.example.aichat.domain.model

/**
 * Статистика использования токенов
 */
data class TokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)

/**
 * Результат отправки сообщения с информацией о токенах
 */
data class ChatResponse(
    val message: Message,
    val tokenUsage: TokenUsage?
)

/**
 * Накопленная статистика токенов за сессию
 */
data class TokenStats(
    val totalPromptTokens: Int = 0,
    val totalCompletionTokens: Int = 0,
    val totalTokens: Int = 0,
    val requestCount: Int = 0,
    val compressionCount: Int = 0,       // Сколько раз выполнялось сжатие
    val savedMessagesCount: Int = 0      // Сколько сообщений было сжато
) {
    fun addUsage(usage: TokenUsage?): TokenStats {
        if (usage == null) return this.copy(requestCount = requestCount + 1)
        return copy(
            totalPromptTokens = totalPromptTokens + usage.promptTokens,
            totalCompletionTokens = totalCompletionTokens + usage.completionTokens,
            totalTokens = totalTokens + usage.totalTokens,
            requestCount = requestCount + 1
        )
    }

    fun recordCompression(compressedCount: Int): TokenStats {
        return copy(
            compressionCount = compressionCount + 1,
            savedMessagesCount = savedMessagesCount + compressedCount
        )
    }
}
