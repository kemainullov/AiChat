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

    private val systemPrompt = """
        Ты - опытный шеф-повар и диетолог. Твоя задача - подобрать идеальное основное блюдо на ужин для пользователя.

        ПРОЦЕСС РАБОТЫ:
        1. Поприветствуй пользователя и объясни, что поможешь подобрать блюдо на ужин
        2. Задай столько вопросов, сколько понадобится для формирования финального ответа, но постарайся подготовить его как можно быстрее. Задавай уточняющие вопросы по одному (например про предпочитаемую кухню, наличие аллергии, доступное время и тому подобное), чтобы собрать полную информацию
        3. После получения необходимого минимума ответов, составь структурированное ТЗ

        КРИТЕРИЙ ЗАВЕРШЕНИЯ:
        Когда ты соберешь достаточно информации, начни свой ответ с маркера "===РЕЗУЛЬТАТ===" и предложи конкретное блюдо.

        ВАЖНО:
        - Задавай вопросы по одному
        - Будь дружелюбным и энтузиастичным
        - Учитывай все ограничения и предпочтения пользователя
        - Предлагай реалистичное блюдо, которое можно приготовить дома
        - Обязательно используй маркер "===РЕЗУЛЬТАТ===" перед финальной рекомендацией
    """.trimIndent()

    override suspend fun sendMessage(messages: List<Message>): Result<Message> {
        return try {
            // Добавляем системное сообщение в начало
            val systemMessage = MessageDto(
                role = "system",
                content = systemPrompt
            )

            val messageDtos = listOf(systemMessage) + messages.map { it.toDto() }

            val request = ChatRequestDto(
                messages = messageDtos
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
