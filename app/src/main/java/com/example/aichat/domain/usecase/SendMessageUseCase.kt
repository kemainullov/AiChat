package com.example.aichat.domain.usecase

import com.example.aichat.domain.model.ChatResponse
import com.example.aichat.domain.model.Message
import com.example.aichat.domain.repository.ChatRepository

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(messages: List<Message>): Result<ChatResponse> {
        return repository.sendMessageWithUsage(messages)
    }
}
