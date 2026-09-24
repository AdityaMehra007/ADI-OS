package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.TitanApplication
import com.example.data.TitanDatabase
import com.example.repository.TitanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * ViewModelProvider.Factory implementation to inject Room database and repository
 * instances into ViewModels (e.g. TitanViewModel) for clean dependency injection
 * and robust state management.
 */
class TitanViewModelFactory(
  private val application: Application,
  private val repository: TitanRepository,
  private val database: TitanDatabase
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(TitanViewModel::class.java)) {
      return TitanViewModel(
        application = application,
        repository = repository,
        database = database
      ) as T
    }
    if (modelClass.isAssignableFrom(CareerNotesViewModel::class.java)) {
      return CareerNotesViewModel(
        repository = repository
      ) as T
    }
    if (modelClass.isAssignableFrom(CompanyBookmarksViewModel::class.java)) {
      return CompanyBookmarksViewModel(
        repository = repository
      ) as T
    }
    if (modelClass.isAssignableFrom(TaskLogsViewModel::class.java)) {
      return TaskLogsViewModel(
        repository = repository
      ) as T
    }
    if (modelClass.isAssignableFrom(CareerStrategyViewModel::class.java)) {
      return CareerStrategyViewModel(
        repository = repository
      ) as T
    }
    if (modelClass.isAssignableFrom(CompanyIntelViewModel::class.java)) {
      return CompanyIntelViewModel(
        repository = repository
      ) as T
    }
    if (modelClass.isAssignableFrom(JobAutomationViewModel::class.java)) {
      return JobAutomationViewModel(
        repository = repository,
        application = application
      ) as T
    }
    if (modelClass.isAssignableFrom(ResumeLabViewModel::class.java)) {
      return ResumeLabViewModel(
        repository = repository
      ) as T
    }
    if (modelClass.isAssignableFrom(ExecutiveCommandViewModel::class.java)) {
      return ExecutiveCommandViewModel(
        repository = repository
      ) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
  }

  companion object {
    /**
     * Helper to create or retrieve the TitanViewModelFactory using the Application context.
     * Uses TitanApplication single source of truth if available, or falls back to
     * initializing database and repository instances safely.
     */
    fun createFactory(application: Application): TitanViewModelFactory {
      val titanApp = application as? TitanApplication
      val database = titanApp?.database ?: TitanDatabase.getDatabase(
        application,
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
      )
      val repository = titanApp?.repository ?: TitanRepository(database)
      return TitanViewModelFactory(
        application = application,
        repository = repository,
        database = database
      )
    }
  }
}
