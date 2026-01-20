package com.example.aichat.data.remote.dto

import com.google.gson.annotations.SerializedName

// OAuth Token Response
data class GigaChatTokenResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_at")
    val expiresAt: Long
)

// Chat Request
data class GigaChatRequestDto(
    @SerializedName("model")
    val model: String,
    @SerializedName("messages")
    val messages: List<GigaChatMessageDto>,
    @SerializedName("stream")
    val stream: Boolean = false
)

data class GigaChatMessageDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String
)

// Chat Response
data class GigaChatResponseDto(
    @SerializedName("choices")
    val choices: List<GigaChatChoiceDto>,
    @SerializedName("created")
    val created: Long,
    @SerializedName("model")
    val model: String,
    @SerializedName("usage")
    val usage: GigaChatUsageDto?
)

data class GigaChatChoiceDto(
    @SerializedName("message")
    val message: GigaChatMessageDto,
    @SerializedName("index")
    val index: Int,
    @SerializedName("finish_reason")
    val finishReason: String?
)

data class GigaChatUsageDto(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)
