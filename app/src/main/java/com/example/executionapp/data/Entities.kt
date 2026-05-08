package com.example.executionapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val currentAction: String = "",
    val resistance: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val preInput: String = ""
)

enum class StepStatus {
    CURRENT,
    COMPLETED,
    SKIPPED
}

@Entity(tableName = "steps")
data class Step(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val stepNumber: Int,
    val content: String,
    val status: StepStatus = StepStatus.CURRENT,
    val completedAt: Long = 0L,
    val durationMillis: Long = 0L
)

@Entity(tableName = "pending_requests")
data class PendingRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val stepNumber: Int,
    val requestType: String,
    val payload: String
)
