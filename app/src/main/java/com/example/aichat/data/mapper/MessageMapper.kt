package com.example.aichat.data.mapper

import com.example.aichat.data.remote.dto.MessageDto
import com.example.aichat.domain.model.Message

fun Message.toDto(): MessageDto {
    return MessageDto(
        role = if (isFromUser) "user" else "assistant",
        content = content
    )
}

fun MessageDto.toDomain(): Message {
    return Message(
        content = content,
        isFromUser = role == "user"
    )
}
