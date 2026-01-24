package com.example.aichat.domain.repository

import com.example.aichat.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(messages: List<Message>): Result<Message>

    fun getMessages(): Flow<List<Message>>

    suspend fun saveMessage(message: Message)

    suspend fun clearHistory()
}
