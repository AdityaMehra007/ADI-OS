package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TitanApplication
import com.example.ai.CareerStrategyAdviceRequest
import com.example.ai.CareerStrategyAdviceResponse
import com.example.ai.CareerStrategyMode
import com.example.data.TitanDatabase
import com.example.data.model.CareerMilestone
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.CareerVelocityReport
import com.example.data.model.GoalItem
import com.example.data.model.SkillItem
import com.example.data.model.UserProfile
import com.example.data.repository.CareerVelocityDataProvider
import com.example.repository.TitanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Domain ViewModel focused on Career Strategy, Roadmap visualization,
 * Milestone tracking, Career Velocity telemetry, and Scenario Simulation.
 */
class CareerStrategyViewModel(
  private val repository: TitanRepository
) : ViewModel() {

  constructor(application: Application) : this(
    (application as? TitanApplication)?.repository ?: TitanRepository(
      (application as? TitanApplication)?.database ?: TitanDatabase.getDatabase(
        application,
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
      )
    )
  )

  val userProfile: StateFlow<UserProfile?> = repository.userProfileFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val milestones: StateFlow<List<CareerMilestone>> = repository.milestonesFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val goals: StateFlow<List<GoalItem>> = repository.goalsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val skills: StateFlow<List<SkillItem>> = repository.skillsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _strategyRoadmap = MutableStateFlow<CareerStrategyRoadmap?>(null)
  val strategyRoadmap: StateFlow<CareerStrategyRoadmap?> = _strategyRoadmap.asStateFlow()

  private val _careerVelocityReport = MutableStateFlow<CareerVelocityReport?>(null)
  val careerVelocityReport: StateFlow<CareerVelocityReport?> = _careerVelocityReport.asStateFlow()

  private val _aiAdviceResponse = MutableStateFlow<CareerStrategyAdviceResponse?>(null)
  val aiAdviceResponse: StateFlow<CareerStrategyAdviceResponse?> = _aiAdviceResponse.asStateFlow()

  private val _isGeneratingStrategy = MutableStateFlow(false)
  val isGeneratingStrategy: StateFlow<Boolean> = _isGeneratingStrategy.asStateFlow()

  init {
    loadInitialVelocityReport()
  }

  private fun loadInitialVelocityReport() {
    viewModelScope.launch {
      _careerVelocityReport.value = CareerVelocityDataProvider.getDefaultCareerVelocityReport()
    }
  }

  fun requestCareerStrategyAdvice(
    questionOrGoal: String,
    mode: CareerStrategyMode = CareerStrategyMode.EXECUTIVE,
    targetRole: String = "Lead Strategy & Operations / Chief of Staff",
    targetHorizonYears: Int = 2
  ) {
    viewModelScope.launch {
      _isGeneratingStrategy.value = true
      try {
        val request = CareerStrategyAdviceRequest(
          questionOrGoal = questionOrGoal,
          mode = mode,
          targetRole = targetRole,
          timeHorizonYears = targetHorizonYears
        )
        val result = repository.geminiService.adviseCareerStrategy(request)
        if (result.isSuccess) {
          _aiAdviceResponse.value = result.getOrNull()
        }
      } finally {
        _isGeneratingStrategy.value = false
      }
    }
  }

  fun toggleMilestoneCompletion(milestoneId: String, currentCompleted: Boolean) {
    viewModelScope.launch {
      val milestone = milestones.value.find { it.id == milestoneId }
      if (milestone != null) {
        val updated = milestone.copy(
          status = if (currentCompleted) "IN_PROGRESS" else "ACHIEVED"
        )
        repository.insertMilestone(updated)
      }
    }
  }

  fun toggleBrutalMode(enabled: Boolean) {
    viewModelScope.launch {
      repository.toggleBrutalMode(enabled)
    }
  }
}
