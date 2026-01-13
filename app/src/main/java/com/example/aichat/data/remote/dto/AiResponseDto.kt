package com.example.aichat.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Структурированный ответ от AI в формате JSON.
 * Этот формат задаётся через system prompt.
 */
data class AiResponseDto(
    @SerializedName("answer")
    val answer: String,
    @SerializedName("mood")
    val mood: String,
    @SerializedName("topics")
    val topics: List<String>
)
