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
        val systemPrompt = "你是一个目标合理性检验助手。请对用户的目标提出具体的修改意见，让它更清晰、更易于执行。注意：在优化目标时，绝对不能涉及任何与现实具体时间（如几点去做什么）相关的内容。哪怕目标已经不错，你也必须给出一个修改版。直接回复修改版的内容，不要有其他废话，不要以'建议修改为：'开头。"
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
            append("提供下一步的小任务。\n")
            append("【核心规则】\n")
            append("1. 每个小目标要有区别，体现循序渐进一步一步达成。\n")
            append("2. 每一步和上一步必须有新内容，绝不能重复。\n")
            append("3. 一定要把用户输入的大目标设定为六步做完之后才能达成的，绝不能一开始就让用户直接完成大目标。\n")
            if (isFinalStep) {
                append("这是第6步，也是最后一步。请引导用户完成最终的目标动作。\n")
            } else {
                append("当前不是最后一步，仅提供当前的过渡性小任务。\n")
            }
            append("【格式】绝对禁止任何前置铺垫（如'基于您的反馈'）、背景说明或鼓励话语。不要分条列点（不要出现1. 2. 3.）。请将内容压缩在50字以内，输出一段简洁无冗余的指令文本，只包含具体要做什么。")
            
            if (isTestGroup) {
                append("（测试组追加要求：确保该动作可在10分钟内落地，绝对不含废话）")
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
                append("用户对上一个步骤选择了“换一个”。")
                if (!replaceReason.isNullOrBlank()) {
                    append(" 困难是：$replaceReason。")
                }
                append("请重新生成当前步骤。")
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
