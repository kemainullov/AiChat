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

data class ChoiceDto(
    @SerializedName("index")
    val index: Int,
    @SerializedName("message")
    val message: MessageDto,
    @SerializedName("finish_reason")
    val finishReason: String?
)
