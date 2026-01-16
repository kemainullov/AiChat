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

    override suspend fun sendMessage(messages: List<Message>, systemPrompt: String?): Result<Message> {
        return try {
            val allMessages = mutableListOf<MessageDto>()

            // Add system prompt as the first message if provided
            if (!systemPrompt.isNullOrBlank()) {
                allMessages.add(MessageDto(role = "system", content = systemPrompt))
            }

            // Add conversation messages
            allMessages.addAll(messages.map { it.toDto() })

            val request = ChatRequestDto(
                messages = allMessages
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
