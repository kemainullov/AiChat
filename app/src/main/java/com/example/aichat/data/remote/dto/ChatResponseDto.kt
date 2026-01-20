package com.example.aichat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("created")
    val created: Long,
    @SerializedName("model")
    val model: String,
    @SerializedName("choices")
    val choices: List<ChoiceDto>,
    @SerializedName("usage")
    val usage: UsageDto? = null
)

data class UsageDto(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)

data class ChoiceDto(
    @SerializedName("index")
    val index: Int,
    @SerializedName("message")
    val message: MessageDto,
    @SerializedName("finish_reason")
    val finishReason: String?
)
