package com.example.executionapp.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.executionapp.data.AppDatabase
import com.example.executionapp.data.Goal
import com.example.executionapp.data.PreferencesManager
import com.example.executionapp.data.Step
import com.example.executionapp.data.StepStatus
import com.example.executionapp.network.ApiClient
import com.example.executionapp.network.LlmRepository
import com.example.executionapp.network.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppScreen {
    INITIAL, VALIDATION, CARDS, SUMMARY
}

enum class ConnectionStatus {
    CHECKING, CONNECTED, DISCONNECTED
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val goalDao = db.goalDao()
    private val stepDao = db.stepDao()
    private val preferencesManager = PreferencesManager(application)
    private val llmRepository = LlmRepository(ApiClient.llmApiService)

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.CHECKING)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.INITIAL)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentGoal = MutableStateFlow<Goal?>(null)
    val currentGoal: StateFlow<Goal?> = _currentGoal.asStateFlow()

    private val _currentStep = MutableStateFlow<Step?>(null)
    val currentStep: StateFlow<Step?> = _currentStep.asStateFlow()

    private val _completedSteps = MutableStateFlow<List<Step>>(emptyList())
    val completedSteps: StateFlow<List<Step>> = _completedSteps.asStateFlow()

    private val _validationMessage = MutableStateFlow<String?>(null)
    val validationMessage: StateFlow<String?> = _validationMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _summaryMessage = MutableStateFlow<String?>(null)
    val summaryMessage: StateFlow<String?> = _summaryMessage.asStateFlow()

    // Timer State
    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis: StateFlow<Long> = _elapsedMillis.asStateFlow()

    private var timerJob: Job? = null
    private var stepStartTime: Long = 0L

    init {
        checkConnectivity()
        checkUnfinishedGoal()
    }

    private fun checkConnectivity() {
        viewModelScope.launch {
            val isConnected = llmRepository.checkConnectivity()
            if (isConnected) {
                _connectionStatus.value = ConnectionStatus.CONNECTED
            } else {
                _connectionStatus.value = ConnectionStatus.DISCONNECTED
            }
        }
    }

    private fun checkUnfinishedGoal() {
        viewModelScope.launch {
            val unfinishedGoal = goalDao.getLatestIncompleteGoal()
            if (unfinishedGoal != null) {
                // Show prompt to continue or start new
                _currentGoal.value = unfinishedGoal
                // Temporarily just setting it up. Actual UI will show a dialog.
            }
        }
    }

    fun continueUnfinishedGoal(goal: Goal) {
        viewModelScope.launch {
            _currentGoal.value = goal
            val steps = stepDao.getStepsForGoal(goal.id).first()
            _completedSteps.value = steps.filter { it.status == StepStatus.COMPLETED }
            val lastStep = steps.lastOrNull()
            if (lastStep?.status == StepStatus.CURRENT) {
                _currentStep.value = lastStep
                startTimer()
                _currentScreen.value = AppScreen.CARDS
            } else {
                generateNextStep()
                _currentScreen.value = AppScreen.CARDS
            }
        }
    }

    fun startNewGoal(goalName: String, currentAction: String, resistance: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val validationResult = llmRepository.validateGoal(goalName, currentAction, resistance)
            _isLoading.value = false

            when (validationResult) {
                is Result.Success -> {
                    val response = validationResult.data
                    if (response.contains("合理")) {
                        proceedToCreateGoal(goalName, currentAction, resistance)
                    } else {
                        _validationMessage.value = response
                        _currentScreen.value = AppScreen.VALIDATION
                    }
                }
                is Result.Error -> {
                    // Fallback to proceed if network error or just show error
                    proceedToCreateGoal(goalName, currentAction, resistance)
                }
            }
        }
    }

    fun proceedToCreateGoal(goalName: String, currentAction: String, resistance: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _validationMessage.value = null
            
            val preInput = preferencesManager.preInputFlow.first()
            val newGoal = Goal(
                name = goalName,
                currentAction = currentAction,
                resistance = resistance,
                preInput = preInput
            )
            val goalId = goalDao.insertGoal(newGoal)
            _currentGoal.value = newGoal.copy(id = goalId)
            
            _completedSteps.value = emptyList()
            generateNextStep()
            _currentScreen.value = AppScreen.CARDS
        }
    }

    private suspend fun generateNextStep(isReplace: Boolean = false, replaceReason: String? = null) {
        val goal = _currentGoal.value ?: return
        val stepNum = if (isReplace) {
            _currentStep.value?.stepNumber ?: 1
        } else {
            (_completedSteps.value.size) + 1
        }

        if (stepNum > 6) {
            finishGoal()
            return
        }

        _isLoading.value = true
        val result = llmRepository.generateNextStep(
            goal = goal.name,
            currentAction = goal.currentAction,
            resistance = goal.resistance,
            preInput = goal.preInput,
            completedSteps = _completedSteps.value.map { it.content },
            isFinalStep = stepNum == 6,
            isReplace = isReplace,
            replaceReason = replaceReason
        )
        _isLoading.value = false

        when (result) {
            is Result.Success -> {
                val newStep = Step(
                    goalId = goal.id,
                    stepNumber = stepNum,
                    content = result.data,
                    status = StepStatus.CURRENT
                )
                val stepId = stepDao.insertStep(newStep)
                _currentStep.value = newStep.copy(id = stepId)
                startTimer()
            }
            is Result.Error -> {
                // TODO: handle error (offline queue)
            }
        }
    }

    fun completeCurrentStep() {
        val step = _currentStep.value ?: return
        viewModelScope.launch {
            stopTimer()
            val duration = SystemClock.elapsedRealtime() - stepStartTime
            val completedStep = step.copy(
                status = StepStatus.COMPLETED,
                completedAt = System.currentTimeMillis(),
                durationMillis = duration
            )
            stepDao.updateStep(completedStep)
            
            val currentList = _completedSteps.value.toMutableList()
            currentList.add(completedStep)
            _completedSteps.value = currentList

            if (completedStep.stepNumber == 6) {
                finishGoal()
            } else {
                generateNextStep()
            }
        }
    }

    fun skipCurrentStep(reason: String) {
        val step = _currentStep.value ?: return
        viewModelScope.launch {
            stopTimer()
            val skippedStep = step.copy(
                status = StepStatus.SKIPPED,
                completedAt = System.currentTimeMillis()
            )
            stepDao.updateStep(skippedStep)
            generateNextStep(isReplace = true, replaceReason = reason)
        }
    }

    private suspend fun finishGoal() {
        val goal = _currentGoal.value ?: return
        goalDao.updateGoal(goal.copy(isCompleted = true))
        
        _isLoading.value = true
        val steps = _completedSteps.value
        val totalTime = steps.sumOf { it.durationMillis }
        val timeStr = formatDuration(totalTime)
        
        val summaryResult = llmRepository.generateSummary(
            goal.name,
            steps.map { it.content },
            timeStr
        )
        
        if (summaryResult is Result.Success) {
            _summaryMessage.value = summaryResult.data
        } else {
            _summaryMessage.value = "太棒了！你完成了所有的步骤！"
        }
        
        _isLoading.value = false
        _currentScreen.value = AppScreen.SUMMARY
    }

    fun resetToInitial() {
        _currentScreen.value = AppScreen.INITIAL
        _currentGoal.value = null
        _currentStep.value = null
        _completedSteps.value = emptyList()
        _summaryMessage.value = null
        stopTimer()
    }

    // Timer logic
    private fun startTimer() {
        timerJob?.cancel()
        stepStartTime = SystemClock.elapsedRealtime()
        timerJob = viewModelScope.launch {
            while (isActive) {
                _elapsedMillis.value = SystemClock.elapsedRealtime() - stepStartTime
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60))
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
