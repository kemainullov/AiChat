package com.example.aichat.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Статистика использования токенов из ответа API
 */
data class UsageDto(
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)
