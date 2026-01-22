package com.example.aichat.domain.repository

import com.example.aichat.domain.model.ChatResponse
import com.example.aichat.domain.model.Message

interface ChatRepository {
    suspend fun sendMessage(messages: List<Message>): Result<Message>

    /**
     * Отправляет сообщение и возвращает ответ с информацией о токенах
     */
    suspend fun sendMessageWithUsage(messages: List<Message>): Result<ChatResponse>
}
