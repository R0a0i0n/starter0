package com.example.executionapp.network

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("model") val model: String = ApiClient.MODEL_NAME,
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("temperature") val temperature: Double = 0.7
)

data class ChatMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ChatResponse(
    @SerializedName("choices") val choices: List<Choice>?
)

data class Choice(
    @SerializedName("message") val message: ChatMessage?
)

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
