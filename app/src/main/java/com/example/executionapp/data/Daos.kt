package com.example.executionapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE isCompleted = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestIncompleteGoal(): Goal?

    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Long): Goal?
}

@Dao
interface StepDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: Step): Long

    @Update
    suspend fun updateStep(step: Step)

    @Query("SELECT * FROM steps WHERE goalId = :goalId ORDER BY stepNumber ASC, id ASC")
    fun getStepsForGoal(goalId: Long): Flow<List<Step>>

    @Query("SELECT * FROM steps WHERE goalId = :goalId AND status = 'COMPLETED' ORDER BY completedAt ASC")
    fun getCompletedStepsForGoal(goalId: Long): Flow<List<Step>>
}

@Dao
interface PendingRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: PendingRequest)

    @Query("SELECT * FROM pending_requests ORDER BY id ASC")
    suspend fun getAllRequests(): List<PendingRequest>

    @Query("DELETE FROM pending_requests WHERE id = :id")
    suspend fun deleteRequest(id: Long)
}
