package com.example.aichat.data.repository

import com.example.aichat.data.local.dao.MessageDao
import com.example.aichat.data.mapper.toDomain
import com.example.aichat.data.mapper.toDto
import com.example.aichat.data.mapper.toEntity
import com.example.aichat.data.remote.api.DeepSeekApiService
import com.example.aichat.data.remote.dto.ChatRequestDto
import com.example.aichat.domain.model.Message
import com.example.aichat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val apiService: DeepSeekApiService,
    private val messageDao: MessageDao
) : ChatRepository {

    override suspend fun sendMessage(messages: List<Message>): Result<Message> {
        return try {
            val request = ChatRequestDto(
                messages = messages.map { it.toDto() }
            )

            val response = apiService.sendMessage(request)

            val assistantMessage = response.choices.firstOrNull()?.message
                ?: return Result.failure(Exception("No response from AI"))

            Result.success(assistantMessage.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMessages(): Flow<List<Message>> {
        return messageDao.getAllMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveMessage(message: Message) {
        messageDao.insertMessage(message.toEntity())
    }

    override suspend fun clearHistory() {
        messageDao.deleteAllMessages()
    }
}
