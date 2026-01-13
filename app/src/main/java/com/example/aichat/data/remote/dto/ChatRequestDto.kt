package com.example.aichat.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequestDto(
    @SerializedName("model")
    val model: String = "deepseek-chat",
    @SerializedName("messages")
    val messages: List<MessageDto>,
    @SerializedName("stream")
    val stream: Boolean = false
) {
    companion object {
        const val SYSTEM_PROMPT = """You are a helpful AI assistant.
You MUST always respond in the following JSON format:
{
  "answer": "Your detailed answer here",
  "mood": "neutral|friendly|serious|curious",
  "topics": ["topic1", "topic2"]
}

Rules:
- "answer" - your main response text (required)
- "mood" - your emotional tone for this response (required)
- "topics" - list of 1-3 main topics discussed (required)
- Always respond with valid JSON only, no additional text outside JSON
- Use Russian language for the "answer" field when user writes in Russian"""
    }
}
