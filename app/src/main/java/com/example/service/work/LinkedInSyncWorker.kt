package com.example.service.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.TitanDatabase
import com.example.service.LinkedInIntegrationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext

/**
 * Modern Jetpack WorkManager worker for LinkedIn profile and network synchronization.
 * Provides battery-efficient, constrained periodic execution compliant with modern Android.
 */
class LinkedInSyncWorker(
  appContext: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    const val TAG = "LinkedInSyncWorker"
    const val UNIQUE_WORK_NAME = "titan_linkedin_periodic_sync_work"
  }

  override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
    try {
      Log.i(TAG, "Executing periodic LinkedIn sync via WorkManager...")
      val tempScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
      val database = TitanDatabase.getDatabase(applicationContext, tempScope)
      val linkedInService = LinkedInIntegrationService(applicationContext, database)

      val syncResult = linkedInService.syncLinkedInData()

      if (syncResult.isSuccess) {
        val telemetry = syncResult.getOrNull()
        Log.i(TAG, "LinkedIn sync succeeded: ${telemetry?.experiencesCount ?: 0} experiences, ${telemetry?.skillsCount ?: 0} skills updated.")
        Result.success()
      } else {
        val err = syncResult.exceptionOrNull()
        Log.w(TAG, "LinkedIn sync finished with notice: ${err?.message}")
        Result.retry()
      }
    } catch (e: Exception) {
      Log.e(TAG, "LinkedInSyncWorker failed with exception: ${e.message}", e)
      if (runAttemptCount < 3) Result.retry() else Result.failure()
    }
  }
}
