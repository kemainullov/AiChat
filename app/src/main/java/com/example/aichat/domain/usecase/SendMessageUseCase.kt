package com.example.aichat.domain.usecase

import com.example.aichat.domain.model.Message
import com.example.aichat.domain.repository.ChatRepository

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(messages: List<Message>): Result<Message> {
        return repository.sendMessage(messages)
    }
}
