package com.example.service.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.service.JobDiscoveryAutomationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Modern Jetpack WorkManager worker for Job Discovery automation.
 * Replaces legacy AlarmManager + ForegroundService background invocation,
 * ensuring compliance with Android 12+ background start restrictions
 * and Android 13+ exact alarm policies.
 */
class JobDiscoveryWorker(
  appContext: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    const val TAG = "JobDiscoveryWorker"
    const val UNIQUE_WORK_NAME = "titan_job_discovery_periodic_work"
  }

  override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
    try {
      Log.i(TAG, "Starting periodic job discovery sync via WorkManager...")
      val manager = JobDiscoveryAutomationManager.getInstance(applicationContext)
      val result = manager.triggerManualPull(source = "WORK_MANAGER_PERIODIC")

      if (result.success) {
        Log.i(TAG, "Job discovery work completed successfully. Pulled: ${result.jobsPulledCount}, Stored: ${result.newJobsStoredCount}")
        Result.success()
      } else {
        Log.w(TAG, "Job discovery work finished with warning: ${result.message}")
        Result.retry()
      }
    } catch (e: Exception) {
      Log.e(TAG, "Job discovery worker failed with error: ${e.message}", e)
      if (runAttemptCount < 3) Result.retry() else Result.failure()
    }
  }
}
