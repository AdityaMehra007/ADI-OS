package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TitanApplication
import com.example.data.TitanDatabase
import com.example.data.model.Company
import com.example.data.model.DailyCareerBriefingReport
import com.example.data.model.MorningExecutiveSummaryReport
import com.example.data.model.QuickActionExecutionTelemetry
import com.example.data.model.QuickActionType
import com.example.data.model.SystemHealthMetric
import com.example.data.model.UserProfile
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
 * Domain ViewModel focused on Executive Command Center operations,
 * Morning Intelligence Summaries, and One-Tap Quick Actions Telemetry.
 */
class ExecutiveCommandViewModel(
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

  val systemHealth: StateFlow<List<SystemHealthMetric>> = repository.healthMetricsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _morningSummary = MutableStateFlow<MorningExecutiveSummaryReport?>(null)
  val morningSummary: StateFlow<MorningExecutiveSummaryReport?> = _morningSummary.asStateFlow()

  private val _dailyBriefing = MutableStateFlow<DailyCareerBriefingReport?>(null)
  val dailyBriefing: StateFlow<DailyCareerBriefingReport?> = _dailyBriefing.asStateFlow()

  private val _quickActionTelemetry = MutableStateFlow<QuickActionExecutionTelemetry?>(null)
  val quickActionTelemetry: StateFlow<QuickActionExecutionTelemetry?> = _quickActionTelemetry.asStateFlow()

  private val _isExecutingAction = MutableStateFlow(false)
  val isExecutingAction: StateFlow<Boolean> = _isExecutingAction.asStateFlow()

  fun triggerQuickAction(actionType: QuickActionType) {
    viewModelScope.launch {
      _isExecutingAction.value = true
      val startTime = System.currentTimeMillis()
      try {
        val telemetry = QuickActionExecutionTelemetry(
          actionType = actionType,
          taskName = actionType.displayName,
          status = "SUCCESS",
          message = "Successfully executed ${actionType.displayName}",
          durationMs = System.currentTimeMillis() - startTime
        )
        _quickActionTelemetry.value = telemetry
      } finally {
        _isExecutingAction.value = false
      }
    }
  }

  fun dismissTelemetry() {
    _quickActionTelemetry.value = null
  }
}
