package com.example.aichat.data.repository

import com.example.aichat.data.remote.api.DeepSeekApiService
import com.example.aichat.data.remote.api.GigaChatApiService
import com.example.aichat.data.remote.api.GigaChatTokenManager
import com.example.aichat.data.remote.dto.ChatRequestDto
import com.example.aichat.data.remote.dto.GigaChatMessageDto
import com.example.aichat.data.remote.dto.GigaChatRequestDto
import com.example.aichat.data.remote.dto.MessageDto
import com.example.aichat.domain.model.AiModel
import com.example.aichat.domain.model.AiProvider
import com.example.aichat.domain.model.ComparisonResult
import com.example.aichat.domain.repository.ModelComparisonRepository
import kotlin.system.measureTimeMillis

class ModelComparisonRepositoryImpl(
    private val deepSeekApiService: DeepSeekApiService,
    private val gigaChatApiService: GigaChatApiService,
    private val gigaChatTokenManager: GigaChatTokenManager
) : ModelComparisonRepository {

    override suspend fun sendPromptToModel(prompt: String, model: AiModel): ComparisonResult {
        return when (model.provider) {
            AiProvider.DEEPSEEK -> sendToDeepSeek(prompt, model)
            AiProvider.GIGACHAT -> sendToGigaChat(prompt, model)
        }
    }

    private suspend fun sendToDeepSeek(prompt: String, model: AiModel): ComparisonResult {
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

    private suspend fun sendToGigaChat(prompt: String, model: AiModel): ComparisonResult {
        var response = ""
        var inputTokens = 0
        var outputTokens = 0
        var error: String? = null

        val responseTime = measureTimeMillis {
            try {
                val accessToken = gigaChatTokenManager.getAccessToken()
                val request = GigaChatRequestDto(
                    model = model.id,
                    messages = listOf(GigaChatMessageDto(role = "user", content = prompt))
                )
                val apiResponse = gigaChatApiService.chatCompletions(
                    authorization = gigaChatTokenManager.getBearerAuth(accessToken),
                    request = request
                )
                response = apiResponse.choices.firstOrNull()?.message?.content ?: ""
                inputTokens = apiResponse.usage?.promptTokens ?: estimateTokens(prompt)
                outputTokens = apiResponse.usage?.completionTokens ?: estimateTokens(response)
            } catch (e: Exception) {
                error = e.message ?: "Ошибка GigaChat API"
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
