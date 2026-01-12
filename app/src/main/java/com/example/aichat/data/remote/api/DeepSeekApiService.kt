package com.example.aichat.data.remote.api

import com.example.aichat.data.remote.dto.ChatRequestDto
import com.example.aichat.data.remote.dto.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface DeepSeekApiService {
    @POST("chat/completions")
    @Headers("Content-Type: application/json")
    suspend fun sendMessage(
        @Body request: ChatRequestDto
    ): ChatResponseDto
}
