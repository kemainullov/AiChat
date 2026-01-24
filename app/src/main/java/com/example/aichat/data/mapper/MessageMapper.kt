package com.example.aichat.data.mapper

import com.example.aichat.data.local.entity.MessageEntity
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

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        content = content,
        isFromUser = isFromUser,
        timestamp = timestamp
    )
}

fun MessageEntity.toDomain(): Message {
    return Message(
        content = content,
        isFromUser = isFromUser,
        timestamp = timestamp
    )
}
