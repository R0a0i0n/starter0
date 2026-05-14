package com.example.executionapp.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.executionapp.data.AppDatabase
import com.example.executionapp.data.Goal
import com.example.executionapp.data.PlanFileManager
import com.example.executionapp.data.PreferencesManager
import com.example.executionapp.data.Step
import com.example.executionapp.data.StepStatus
import com.example.executionapp.network.ApiClient
import com.example.executionapp.network.GeneratedPlan
import com.example.executionapp.network.LlmRepository
import com.example.executionapp.network.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
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
    private val planFileManager = PlanFileManager(application)
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

    private val _planDocumentContent = MutableStateFlow<String?>(null)
    val planDocumentContent: StateFlow<String?> = _planDocumentContent.asStateFlow()

    private val _isWelcomeDialogDismissed = MutableStateFlow(false)
    val showWelcomeDialog: StateFlow<Boolean> = combine(
        preferencesManager.showWelcomeDialogFlow,
        _isWelcomeDialogDismissed
    ) { prefShow, dismissed ->
        prefShow && !dismissed
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val hasSwipedCard: StateFlow<Boolean> = preferencesManager.hasSwipedCardFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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
            _planDocumentContent.value = planFileManager.loadPlan(goal.id)?.content
            val lastStep = steps.lastOrNull()
            if (lastStep?.status == StepStatus.CURRENT) {
                _currentStep.value = lastStep
                startTimer()
                _currentScreen.value = AppScreen.CARDS
            } else {
                generatePlanAndLoadCurrentStep()
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
                    _validationMessage.value = validationResult.data
                    _currentScreen.value = AppScreen.VALIDATION
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
            generatePlanAndLoadCurrentStep()
            _currentScreen.value = AppScreen.CARDS
        }
    }

    private suspend fun generatePlanAndLoadCurrentStep(
        isReplace: Boolean = false,
        replaceReason: String? = null
    ) {
        val goal = _currentGoal.value ?: return
        val currentStepNumber = if (isReplace) {
            _currentStep.value?.stepNumber ?: 1
        } else {
            (_completedSteps.value.size) + 1
        }

        if (currentStepNumber > 6) {
            finishGoal()
            return
        }

        _isLoading.value = true

        val shouldRegeneratePlan = isReplace && currentStepNumber < 6
        val existingPlan = planFileManager.loadPlan(goal.id)

        val planResult = when {
            existingPlan == null -> {
                llmRepository.generateExecutionPlan(
                    goal = goal.name,
                    currentAction = goal.currentAction,
                    resistance = goal.resistance,
                    preInput = goal.preInput,
                    completedSteps = _completedSteps.value.map { it.content },
                    regenerateFromStep = currentStepNumber,
                    replaceReason = null
                )
            }

            shouldRegeneratePlan -> {
                llmRepository.generateExecutionPlan(
                    goal = goal.name,
                    currentAction = goal.currentAction,
                    resistance = goal.resistance,
                    preInput = goal.preInput,
                    completedSteps = _completedSteps.value.map { it.content },
                    regenerateFromStep = currentStepNumber,
                    replaceReason = replaceReason
                )
            }

            else -> {
                Result.Success(
                    GeneratedPlan(
                        steps = existingPlan.steps,
                        rawContent = existingPlan.content
                    )
                )
            }
        }

        when (planResult) {
            is Result.Success -> {
                val planDocument = planFileManager.savePlan(
                    goalId = goal.id,
                    goalName = goal.name,
                    steps = planResult.data.steps
                )
                _planDocumentContent.value = planDocument.content

                val stepContent = planDocument.steps.getOrNull(currentStepNumber - 1)
                if (stepContent.isNullOrBlank()) {
                    _isLoading.value = false
                    return
                }

                val newStep = Step(
                    goalId = goal.id,
                    stepNumber = currentStepNumber,
                    content = stepContent,
                    status = StepStatus.CURRENT
                )
                val stepId = stepDao.insertStep(newStep)
                _currentStep.value = newStep.copy(id = stepId)
                _isLoading.value = false
                startTimer()
            }

            is Result.Error -> {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadStepFromPlan(stepNumber: Int) {
        val goal = _currentGoal.value ?: return
        if (stepNumber > 6) {
            finishGoal()
            return
        }

        val planDocument = planFileManager.loadPlan(goal.id)
        _planDocumentContent.value = planDocument?.content
        val stepContent = planDocument?.steps?.getOrNull(stepNumber - 1) ?: return

        _isLoading.value = true
        val newStep = Step(
            goalId = goal.id,
            stepNumber = stepNumber,
            content = stepContent,
            status = StepStatus.CURRENT
        )
        val stepId = stepDao.insertStep(newStep)
        _currentStep.value = newStep.copy(id = stepId)
        _isLoading.value = false
        startTimer()
    }

    fun completeCurrentStep() {
        val step = _currentStep.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            preferencesManager.setHasSwipedCard()
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
                loadStepFromPlan(completedStep.stepNumber + 1)
            }
        }
    }

    fun skipCurrentStep(reason: String) {
        val step = _currentStep.value ?: return
        viewModelScope.launch {
            _isLoading.value = true
            preferencesManager.setHasSwipedCard()
            stopTimer()
            val skippedStep = step.copy(
                status = StepStatus.SKIPPED,
                completedAt = System.currentTimeMillis(),
                durationMillis = 0L
            )
            stepDao.updateStep(skippedStep)
            
            if (skippedStep.stepNumber == 6) {
                loadStepFromPlan(6)
            } else {
                generatePlanAndLoadCurrentStep(isReplace = true, replaceReason = reason)
            }
        }
    }

    private suspend fun finishGoal() {
        val goal = _currentGoal.value ?: return
        goalDao.updateGoal(goal.copy(isCompleted = true))

        _summaryMessage.value = buildSummaryMessage(goal.name)
        _currentScreen.value = AppScreen.SUMMARY
        _isLoading.value = false
    }

    private fun buildSummaryMessage(goalName: String): String {
        val normalizedGoal = goalName.trim().trimEnd('。', '！', '!', '?', '？')
        return "恭喜你，现在你可以静下心去实现你的目标“$normalizedGoal”了"
    }

    fun dismissWelcomeDialog(dontShowAgain: Boolean) {
        viewModelScope.launch {
            if (dontShowAgain) {
                preferencesManager.setDontShowWelcomeDialog()
            }
            _isWelcomeDialogDismissed.value = true
        }
    }

    fun ignoreUnfinishedGoal() {
        val goal = _currentGoal.value ?: return
        viewModelScope.launch {
            goalDao.updateGoal(goal.copy(isCompleted = true))
            resetToInitial()
        }
    }

    fun resetToInitial() {
        _currentScreen.value = AppScreen.INITIAL
        _currentGoal.value = null
        _currentStep.value = null
        _completedSteps.value = emptyList()
        _summaryMessage.value = null
        _planDocumentContent.value = null
        _isLoading.value = false
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

    fun formatDuration(millis: Long): String {
        if (millis <= 0) return "总用时 0秒"
        
        val totalSeconds = millis / 1000
        val seconds = totalSeconds % 60
        val minutes = (totalSeconds / 60) % 60
        val hours = (totalSeconds / (60 * 60))
        
        val sb = StringBuilder()
        if (hours > 0) sb.append("${hours}时")
        if (minutes > 0) sb.append("${minutes}分")
        if (seconds > 0 || sb.isEmpty()) sb.append("${seconds}秒")
        
        return "总用时 ${sb.toString()}"
    }
}
