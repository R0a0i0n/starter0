package com.example.executionapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // 预设内置的 API 地址和密钥，请根据实际情况替换
    private const val BASE_URL = "https://api.deepseek.com/"
    const val API_KEY = "sk-423cf1773236424cb0cdc3727b33a542" // REPLACE WITH ACTUAL KEY
    const val MODEL_NAME = "deepseek-v4-flash" // 调用的模型名称

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val llmApiService: LlmApiService = retrofit.create(LlmApiService::class.java)
}
