package com.example.aichat.data.mapper

import com.example.aichat.data.remote.dto.MessageDto
import com.example.aichat.data.remote.dto.UsageDto
import com.example.aichat.domain.model.Message
import com.example.aichat.domain.model.MessageType
import com.example.aichat.domain.model.TokenUsage

fun Message.toDto(): MessageDto {
    val role = when {
        type == MessageType.SYSTEM -> "system"
        type == MessageType.SUMMARY -> "system"  // Summary отправляется как system message
        isFromUser -> "user"
        else -> "assistant"
    }

    // Для summary добавляем префикс, чтобы AI понимал контекст
    val content = if (type == MessageType.SUMMARY) {
        "[Краткое изложение предыдущего диалога ($compressedMessagesCount сообщений)]\n$content"
    } else {
        content
    }

    return MessageDto(
        role = role,
        content = content
    )
}

fun MessageDto.toDomain(): Message {
    return Message(
        content = content,
        isFromUser = role == "user",
        type = when (role) {
            "system" -> MessageType.SYSTEM
            else -> MessageType.REGULAR
        }
    )
}

fun UsageDto.toDomain(): TokenUsage {
    return TokenUsage(
        promptTokens = promptTokens,
        completionTokens = completionTokens,
        totalTokens = totalTokens
    )
}
