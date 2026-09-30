package com.example

import android.app.Application
import com.example.ai.GeminiServiceWrapper
import com.example.ai.IGeminiServiceWrapper
import com.example.data.TitanDatabase
import com.example.repository.TitanRepository
import com.example.ui.viewmodel.TitanViewModelFactory
import androidx.work.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Custom Application class for Adi OS (Titan).
 * Provides centralized, lifecycle-aware instances of Room database,
 * repositories, and ViewModelProvider.Factory for dependency injection.
 */
class TitanApplication : Application(), Configuration.Provider {

  val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setMinimumLoggingLevel(android.util.Log.INFO)
      .build()

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
    try {
      com.example.service.work.TitanWorkManagerHelper.scheduleAllPeriodicTasks(this)
    } catch (e: Throwable) {
      android.util.Log.w("TitanApplication", "Periodic work setup deferred/skipped: ${e.message}")
    }
  }

  companion object {
    lateinit var instance: TitanApplication
      private set
  }
}
