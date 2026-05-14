package com.example.executionapp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class GeneratedPlan(
    val steps: List<String>,
    val rawContent: String
)

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
        val systemPrompt = "你是一个目标优化助手。请对用户的目标提出具体优化，让它更清晰、更易于执行。注意：优化后的目标绝对不能包含任何现实时间安排，例如几点、今天晚上、明天早上。哪怕目标已经不错，你也必须给出一个修改版。直接回复优化后的目标，不要有其他废话。"
        val userPrompt = "目标：$goal\n当前在做：$currentAction\n阻力：$resistance"
        return executeChat(systemPrompt, userPrompt)
    }

    suspend fun generateExecutionPlan(
        goal: String,
        currentAction: String,
        resistance: String,
        preInput: String,
        completedSteps: List<String>,
        regenerateFromStep: Int,
        replaceReason: String? = null
    ): Result<GeneratedPlan> {
        val systemPrompt = buildString {
            append("【角色】你是“滚雪球执行力教练”，负责先生成一份正式的六步总体步骤规划，再按规划执行。\n")
            append("【核心要求】\n")
            append("1. 必须输出完整六步规划，且只输出六行。\n")
            append("2. 六个步骤都必须是系统生成的具体小任务，第6步也必须与前5步保持一致，不能直接照抄用户输入的目标。\n")
            append("3. 第1步到第6步必须循序渐进、彼此不同、每一步都有新的推进内容，绝不能重复。\n")
            append("4. 不得出现任何现实时间安排，例如几点、今晚、明早、下班后。\n")
            append("5. 如果提供了已完成步骤，这些步骤必须原样保留，不得改写。\n")
            append("6. 如果这是“换一个”后的重规划，只能重写指定步骤及其后的剩余步骤。\n")
            append("7. 输出格式固定为六行：第1步：... 到 第6步：...\n")
            append("8. 每一步都只写具体动作，不要解释、不要鼓励、不要加标题。")
        }

        val userPrompt = buildString {
            append("最终目标：$goal\n")
            append("当前动作：$currentAction\n")
            append("阻力：$resistance\n")
            append("这六步都要服务于这个最终目标，但第6步也必须是系统生成的小任务，不要直接复述目标原文。\n")
            append("需要从第${regenerateFromStep}步开始作为当前待执行步骤。\n")
            if (preInput.isNotBlank()) {
                append("用户预输入偏好：$preInput\n")
            }
            if (completedSteps.isNotEmpty()) {
                append("已完成步骤（必须原样保留）：\n")
                completedSteps.forEachIndexed { index, step ->
                    append("第${index + 1}步：$step\n")
                }
            }
            if (!replaceReason.isNullOrBlank()) {
                append("触发“换一个”的原因：$replaceReason\n")
                append("请从当前步骤开始重写后续规划。\n")
            } else {
                append("请生成初始总体步骤规划。\n")
            }
        }

        return when (val result = executeChat(systemPrompt, userPrompt)) {
            is Result.Success -> {
                val parsedSteps = parsePlanSteps(result.data)
                if (parsedSteps == null) {
                    Result.Error(IllegalStateException("规划解析失败"))
                } else {
                    val normalizedSteps = parsedSteps.toMutableList()
                    completedSteps.forEachIndexed { index, step ->
                        if (index < normalizedSteps.size) {
                            normalizedSteps[index] = step.trim()
                        }
                    }
                    if (normalizedSteps.any { it.isBlank() }) {
                        Result.Error(IllegalStateException("规划包含空步骤"))
                    } else {
                        Result.Success(
                            GeneratedPlan(
                                steps = normalizedSteps,
                                rawContent = result.data
                            )
                        )
                    }
                }
            }

            is Result.Error -> Result.Error(result.exception)
        }
    }

    private fun parsePlanSteps(content: String): List<String>? {
        val matches = Regex("""第([1-6])步[：:]\s*(.+)""")
            .findAll(content)
            .mapNotNull { match ->
                val stepNumber = match.groupValues[1].toIntOrNull() ?: return@mapNotNull null
                stepNumber to match.groupValues[2].trim()
            }
            .toList()

        if (matches.size != 6) return null

        val orderedSteps = MutableList(6) { "" }
        matches.forEach { (stepNumber, text) ->
            orderedSteps[stepNumber - 1] = text
        }

        return if (orderedSteps.all { it.isNotBlank() }) orderedSteps else null
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
