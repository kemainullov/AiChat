package com.example.aichat.data.repository

import com.example.aichat.data.mapper.toDomain
import com.example.aichat.data.mapper.toDto
import com.example.aichat.data.remote.api.DeepSeekApiService
import com.example.aichat.data.remote.dto.ChatRequestDto
import com.example.aichat.data.remote.dto.MessageDto
import com.example.aichat.domain.model.Message
import com.example.aichat.domain.repository.ChatRepository

class ChatRepositoryImpl(
    private val apiService: DeepSeekApiService
) : ChatRepository {

    override suspend fun sendMessage(messages: List<Message>): Result<Message> {
        return try {
            // Создаём system message с инструкцией о формате JSON
            val systemMessage = MessageDto(
                role = "system",
                content = ChatRequestDto.SYSTEM_PROMPT
            )

            // Добавляем system prompt в начало списка сообщений
            val messagesWithSystem = listOf(systemMessage) + messages.map { it.toDto() }

            val request = ChatRequestDto(
                messages = messagesWithSystem
            )

            val response = apiService.sendMessage(request)

            val assistantMessage = response.choices.firstOrNull()?.message
                ?: return Result.failure(Exception("No response from AI"))

            Result.success(assistantMessage.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
