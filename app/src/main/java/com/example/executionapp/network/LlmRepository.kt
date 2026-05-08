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
        val systemPrompt = "你是一个目标合理性检验助手。请对用户的目标提出具体的修改意见，让它更清晰、更易于执行。哪怕目标已经不错，你也必须给出一个修改版。直接回复修改版的内容，不要有其他废话，不要以'建议修改为：'开头。"
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
        replaceReason: String? = null,
        isTestGroup: Boolean = false
    ): Result<String> {
        val systemPrompt = buildString {
            append("【场景】用户正在使用“滚雪球”执行力应用，试图克服阻力完成一个大目标。\n")
            append("【角色】你是“滚雪球执行力教练”，负责将大目标拆解为极小、极易完成的动作。\n")
            append("【任务】基于用户的大目标：$goal，以及当前正在做的动作：$currentAction。")
            if (preInput.isNotBlank()) append(" 结合用户的预输入偏好：$preInput。")
            append("让新旧动作无缝重叠，提供下一步的小任务。")
            if (isFinalStep) {
                append("这是第6步，也是最后一步。请引导用户完成最终的目标动作。")
            }
            append("\n【格式】强制输出可执行的动作清单，绝对禁止任何抽象的建议或鼓励话语。")
            
            if (isTestGroup) {
                append("请给出 1条可在 10 分钟内落地的具体动作，不说废话。")
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

        android.util.Log.d("AB_TEST_LOG", "generateNextStep: isTestGroup=$isTestGroup")
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
