package com.example.aichat.domain.usecase

import com.example.aichat.domain.repository.ChatRepository

class ClearHistoryUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke() {
        repository.clearHistory()
    }
}
