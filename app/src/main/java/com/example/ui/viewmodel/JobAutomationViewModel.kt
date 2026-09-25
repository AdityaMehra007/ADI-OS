package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.TitanApplication
import com.example.data.TitanDatabase
import com.example.data.model.Application as JobApplication
import com.example.data.model.AutomationCriteria
import com.example.data.model.AutomationRule
import com.example.data.model.AutomationTask
import com.example.data.model.Job
import com.example.repository.TitanRepository
import com.example.service.JobDiscoveryAutomationManager
import com.example.service.work.TitanWorkManagerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Domain ViewModel focused on Job Opportunity Tracking, Automated Portals Discovery,
 * Application Pipelines, and Background WorkManager Daemons.
 */
class JobAutomationViewModel(
  private val repository: TitanRepository,
  private val application: Application
) : ViewModel() {

  constructor(application: Application) : this(
    (application as? TitanApplication)?.repository ?: TitanRepository(
      (application as? TitanApplication)?.database ?: TitanDatabase.getDatabase(
        application,
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
      )
    ),
    application
  )

  private val automationManager = JobDiscoveryAutomationManager.getInstance(application)

  val allJobs: StateFlow<List<Job>> = repository.jobsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allApplications: StateFlow<List<JobApplication>> = repository.applicationsFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val automationTasks: StateFlow<List<AutomationTask>> = repository.getAllAutomationTasks()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val automationRules: StateFlow<List<AutomationRule>> = repository.automationRulesFlow
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val isDiscoveryRunning: StateFlow<Boolean> = automationManager.isServiceRunning
  val isPeriodicDiscoveryActive: StateFlow<Boolean> = automationManager.isPeriodicEnabled

  private val _searchFilter = MutableStateFlow("")
  val searchFilter: StateFlow<String> = _searchFilter.asStateFlow()

  val filteredJobs: StateFlow<List<Job>> = combine(allJobs, _searchFilter) { jobs, query ->
    if (query.isBlank()) jobs
    else jobs.filter { job ->
      job.title.contains(query, ignoreCase = true) ||
        job.companyName.contains(query, ignoreCase = true) ||
        job.location.contains(query, ignoreCase = true) ||
        job.department.contains(query, ignoreCase = true)
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setSearchFilter(query: String) {
    _searchFilter.value = query
  }

  fun triggerManualJobScan(source: String = "UI_MANUAL_BUTTON") {
    viewModelScope.launch {
      automationManager.triggerManualPull(source = source)
    }
  }

  fun updateAutomationCriteria(criteria: AutomationCriteria) {
    automationManager.updateCriteria(criteria)
  }

  fun setPeriodicAutomationEnabled(enabled: Boolean) {
    automationManager.setPeriodicEnabled(enabled)
    if (enabled) {
      TitanWorkManagerHelper.scheduleAllPeriodicTasks(application)
    } else {
      TitanWorkManagerHelper.cancelAllPeriodicTasks(application)
    }
  }

  fun toggleRuleActive(ruleId: String, currentActive: Boolean) {
    viewModelScope.launch {
      repository.toggleAutomationRule(ruleId, !currentActive)
    }
  }

  fun updateApplicationStatus(applicationId: String, newStatus: String) {
    viewModelScope.launch {
      repository.updateApplicationStatus(applicationId, newStatus)
    }
  }
}
