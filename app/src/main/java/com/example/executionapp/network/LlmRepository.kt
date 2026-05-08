package com.example.executionapp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LlmRepository(private val apiService: LlmApiService) {

    private val authHeader = "Bearer ${ApiClient.API_KEY}"

    suspend fun checkConnectivity(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val socket = java.net.Socket()
                // Connect to api.deepseek.com on port 443 with a timeout of 5 seconds
                socket.connect(java.net.InetSocketAddress("api.deepseek.com", 443), 5000)
                socket.close()
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun validateGoal(goal: String, currentAction: String, resistance: String): Result<String> {
        val systemPrompt = "你是一个目标合理性检验助手。判断用户的目标是否清晰、可完成、合理。如果不合理（例如'我要赚大钱'），指出问题并给出一个修改建议，以'建议修改为：xxx'的格式回复，并询问是否修改。如果目标合理，直接回复'合理'两个字。不要有其他废话。"
        val userPrompt = "目标：$goal\n当前在做：$currentAction\n阻力：$resistance"
        return executeChat(systemPrompt, userPrompt)
    }

    suspend fun generateNextStep(
        goal: String,
        currentAction: String,
        resistance: String,
        preInput: String,
        completedSteps: List<String>,
        isFinalStep: Boolean,
        isReplace: Boolean = false,
        replaceReason: String? = null
    ): Result<String> {
        val systemPrompt = buildString {
            append("你是“滚雪球执行力教练”。用户总目标是：$goal。")
            if (preInput.isNotBlank()) append(" 用户的预输入偏好：$preInput。")
            append("你一次只能给出【下一步】的小任务。任务描述不能超过 3 句话，且尽量能在三分钟内完成。")
            append("请利用用户当前正在做的动作（$currentAction），让新旧动作无缝重叠。")
            if (isFinalStep) {
                append("这是第6步，也是最后一步（最终目标）。请引导用户完成最终的目标动作。")
            }
        }
        
        val userPrompt = buildString {
            if (completedSteps.isNotEmpty()) {
                append("已完成步骤：\n")
                completedSteps.forEachIndexed { index, step ->
                    append("${index + 1}. $step\n")
                }
            }
            if (isReplace) {
                append("用户对你上一次给的步骤选择了“换一个”。")
                if (!replaceReason.isNullOrBlank()) {
                    append(" 用户的困难/原因是：$replaceReason。")
                }
                append("请根据以上信息，重新生成当前步骤，换一种更简单或更合适的做法。")
            } else {
                append("请给出下一步的小任务。")
            }
        }

        return executeChat(systemPrompt, userPrompt)
    }

    suspend fun generateSummary(goal: String, completedSteps: List<String>, totalTimeStr: String): Result<String> {
        val systemPrompt = "你是“滚雪球执行力教练”。用户刚刚完成了一个目标，请给出简短的总结回顾和鼓励。"
        val userPrompt = "目标：$goal\n总耗时：$totalTimeStr\n完成步骤：\n${completedSteps.joinToString("\n")}\n请生成鼓励总结（如'你太棒了！从...到...，你一步步滚到了终点！'）"
        return executeChat(systemPrompt, userPrompt)
    }

    private suspend fun executeChat(systemPrompt: String, userPrompt: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                android.util.Log.d("LlmDialogue", "====== API Request ======\nSystem: $systemPrompt\nUser: $userPrompt")
                
                val request = ChatRequest(
                    messages = listOf(
                        ChatMessage("system", systemPrompt),
                        ChatMessage("user", userPrompt)
                    )
                )
                val response = apiService.createChatCompletion(authHeader, request)
                if (response.isSuccessful) {
                    val content = response.body()?.choices?.firstOrNull()?.message?.content ?: "未能生成内容"
                    android.util.Log.d("LlmDialogue", "====== API Response ======\nAssistant: $content")
                    Result.Success(content.trim())
                } else {
                    val errorMsg = "API Error: ${response.code()} ${response.message()}"
                    android.util.Log.e("LlmDialogue", "====== API Error ======\n$errorMsg")
                    Result.Error(Exception(errorMsg))
                }
            } catch (e: Exception) {
                android.util.Log.e("LlmDialogue", "====== API Exception ======\n${e.message}", e)
                Result.Error(e)
            }
        }
    }
}
