package com.example.aichat.domain.usecase

import com.example.aichat.domain.model.Message
import com.example.aichat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<List<Message>> {
        return repository.getMessages()
    }
}
