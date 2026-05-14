package com.example.executionapp.data

import android.content.Context
import java.io.File

data class ExecutionPlanDocument(
    val goalId: Long,
    val goalName: String,
    val steps: List<String>,
    val content: String
)

class PlanFileManager(context: Context) {

    private val plansDir = File(context.filesDir, "plans").apply { mkdirs() }

    fun savePlan(goalId: Long, goalName: String, steps: List<String>): ExecutionPlanDocument {
        require(steps.size == 6) { "Execution plan must contain exactly 6 steps." }

        val content = buildString {
            appendLine("任务总体步骤规划文件")
            appendLine("目标：$goalName")
            appendLine()
            steps.forEachIndexed { index, step ->
                appendLine("步骤${index + 1}：${step.trim()}")
            }
        }.trim()

        getPlanFile(goalId).writeText(content)

        return ExecutionPlanDocument(
            goalId = goalId,
            goalName = goalName,
            steps = steps.map { it.trim() },
            content = content
        )
    }

    fun loadPlan(goalId: Long): ExecutionPlanDocument? {
        val file = getPlanFile(goalId)
        if (!file.exists()) return null

        val content = file.readText()
        val lines = content.lines()
        val goalName = lines.firstOrNull { it.startsWith("目标：") }
            ?.substringAfter("目标：")
            ?.trim()
            .orEmpty()

        val steps = lines.mapNotNull { line ->
            if (!line.startsWith("步骤")) return@mapNotNull null
            line.substringAfter("：", "").trim()
        }

        if (steps.size != 6) return null

        return ExecutionPlanDocument(
            goalId = goalId,
            goalName = goalName,
            steps = steps,
            content = content
        )
    }

    fun deletePlan(goalId: Long) {
        getPlanFile(goalId).delete()
    }

    private fun getPlanFile(goalId: Long): File {
        return File(plansDir, "goal_${goalId}_plan.txt")
    }
}
