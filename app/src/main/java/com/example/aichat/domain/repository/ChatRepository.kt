package com.example.aichat.domain.repository

import com.example.aichat.domain.model.Message

interface ChatRepository {
    suspend fun sendMessage(messages: List<Message>, temperature: Float = 0.7f): Result<Message>
}
