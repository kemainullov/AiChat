package com.example.aichat.data.repository

import com.example.aichat.data.remote.api.DeepSeekApiService
import com.example.aichat.data.remote.dto.ChatRequestDto
import com.example.aichat.data.remote.dto.MessageDto
import com.example.aichat.domain.model.AiModel
import com.example.aichat.domain.model.ComparisonResult
import com.example.aichat.domain.repository.ModelComparisonRepository
import kotlin.system.measureTimeMillis

class ModelComparisonRepositoryImpl(
    private val deepSeekApiService: DeepSeekApiService
) : ModelComparisonRepository {

    override suspend fun sendPromptToModel(prompt: String, model: AiModel): ComparisonResult {
        var response = ""
        var inputTokens = 0
        var outputTokens = 0
        var error: String? = null

        val responseTime = measureTimeMillis {
            try {
                val request = ChatRequestDto(
                    model = model.id,
                    messages = listOf(MessageDto(role = "user", content = prompt))
                )
                val apiResponse = deepSeekApiService.sendMessage(request)
                response = apiResponse.choices.firstOrNull()?.message?.content ?: ""
                inputTokens = apiResponse.usage?.promptTokens ?: estimateTokens(prompt)
                outputTokens = apiResponse.usage?.completionTokens ?: estimateTokens(response)
            } catch (e: Exception) {
                error = e.message ?: "Ошибка DeepSeek API"
            }
        }

        return ComparisonResult(
            model = model,
            response = response,
            responseTimeMs = responseTime,
            inputTokens = inputTokens,
            outputTokens = outputTokens,
            error = error
        )
    }

    private fun estimateTokens(text: String): Int {
        return (text.length / 4).coerceAtLeast(1)
    }
}
