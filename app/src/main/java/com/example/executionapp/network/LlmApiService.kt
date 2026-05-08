package com.example.executionapp.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LlmApiService {
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>
}
