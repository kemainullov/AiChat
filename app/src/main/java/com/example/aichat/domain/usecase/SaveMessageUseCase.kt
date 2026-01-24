package com.example.aichat.domain.usecase

import com.example.aichat.domain.model.Message
import com.example.aichat.domain.repository.ChatRepository

class SaveMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(message: Message) {
        repository.saveMessage(message)
    }
}
