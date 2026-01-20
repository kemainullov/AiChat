package com.example.aichat.data.remote.api

import com.example.aichat.data.remote.dto.GigaChatRequestDto
import com.example.aichat.data.remote.dto.GigaChatResponseDto
import com.example.aichat.data.remote.dto.GigaChatTokenResponseDto
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface GigaChatAuthService {

    @FormUrlEncoded
    @POST("api/v2/oauth")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Header("Authorization") authorization: String,
        @Header("RqUID") rqUID: String,
        @Field("scope") scope: String = "GIGACHAT_API_PERS"
    ): GigaChatTokenResponseDto
}

interface GigaChatApiService {

    @POST("api/v1/chat/completions")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun chatCompletions(
        @Header("Authorization") authorization: String,
        @Body request: GigaChatRequestDto
    ): GigaChatResponseDto
}
