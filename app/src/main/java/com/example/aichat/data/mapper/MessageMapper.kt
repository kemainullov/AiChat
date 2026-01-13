package com.example.aichat.data.mapper

import com.example.aichat.data.remote.dto.AiResponseDto
import com.example.aichat.data.remote.dto.MessageDto
import com.example.aichat.domain.model.Message
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

private val gson = Gson()

fun Message.toDto(): MessageDto {
    return MessageDto(
        role = if (isFromUser) "user" else "assistant",
        content = content
    )
}

fun MessageDto.toDomain(): Message {
    return if (role == "assistant") {
        parseAiResponse(content)
    } else {
        Message(
            content = content,
            isFromUser = true
        )
    }
}

/**
 * Парсит JSON ответ от AI и извлекает структурированные данные.
 * Если парсинг не удался, возвращает сообщение с сырым контентом.
 */
private fun parseAiResponse(rawContent: String): Message {
    return try {
        val aiResponse = gson.fromJson(rawContent, AiResponseDto::class.java)
        Message(
            content = aiResponse.answer,
            isFromUser = false,
            mood = aiResponse.mood,
            topics = aiResponse.topics
        )
    } catch (e: JsonSyntaxException) {
        // Fallback: если JSON невалидный, используем сырой контент
        Message(
            content = rawContent,
            isFromUser = false
        )
    }
}
