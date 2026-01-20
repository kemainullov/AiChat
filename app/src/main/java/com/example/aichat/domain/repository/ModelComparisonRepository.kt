package com.example.aichat.domain.repository

import com.example.aichat.domain.model.AiModel
import com.example.aichat.domain.model.ComparisonResult

interface ModelComparisonRepository {
    suspend fun sendPromptToModel(prompt: String, model: AiModel): ComparisonResult
}
