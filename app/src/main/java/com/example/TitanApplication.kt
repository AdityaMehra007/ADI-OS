package com.example

import android.app.Application
import com.example.ai.GeminiServiceWrapper
import com.example.ai.IGeminiServiceWrapper
import com.example.data.TitanDatabase
import com.example.repository.TitanRepository
import com.example.ui.viewmodel.TitanViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Custom Application class for Adi OS (Titan).
 * Provides centralized, lifecycle-aware instances of Room database,
 * repositories, and ViewModelProvider.Factory for dependency injection.
 */
class TitanApplication : Application() {

  val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  val database: TitanDatabase by lazy {
    TitanDatabase.getDatabase(this, applicationScope)
  }

  val geminiWrapper: IGeminiServiceWrapper by lazy {
    GeminiServiceWrapper()
  }

  val repository: TitanRepository by lazy {
    TitanRepository(database, geminiWrapper)
  }

  val viewModelFactory: TitanViewModelFactory by lazy {
    TitanViewModelFactory(
      application = this,
      repository = repository,
      database = database
    )
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    com.example.service.work.TitanWorkManagerHelper.scheduleAllPeriodicTasks(this)
  }

  companion object {
    lateinit var instance: TitanApplication
      private set
  }
}
