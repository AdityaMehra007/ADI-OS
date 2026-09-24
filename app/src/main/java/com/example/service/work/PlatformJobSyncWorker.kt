package com.example.service.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.service.JobPlatformSyncEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Modern Jetpack WorkManager worker for cross-platform job synchronization (LinkedIn, Indeed, Naukri).
 * Operates safely within Android Doze constraints and background execution limits.
 */
class PlatformJobSyncWorker(
  appContext: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    const val TAG = "PlatformJobSyncWorker"
    const val UNIQUE_WORK_NAME = "titan_platform_job_periodic_sync_work"
  }

  override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
    try {
      Log.i(TAG, "Executing platform job sync cycle via WorkManager...")
      val engine = JobPlatformSyncEngine.getInstance(applicationContext)
      val syncResult = engine.executeSyncCycle(triggerSource = "WORK_MANAGER_PERIODIC")

      if (syncResult.isSuccess) {
        val count = syncResult.updatedCount
        Log.i(TAG, "Platform job sync succeeded. Updated $count job listings.")
        Result.success()
      } else {
        Log.w(TAG, "Platform job sync completed with notice: ${syncResult.summaryMessage}")
        Result.retry()
      }
    } catch (e: Exception) {
      Log.e(TAG, "PlatformJobSyncWorker failed with exception: ${e.message}", e)
      if (runAttemptCount < 3) Result.retry() else Result.failure()
    }
  }
}
