package com.example.service.work

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Centralized manager for scheduling and monitoring background periodic workers
 * using Android Jetpack WorkManager. Enforces battery, network, and Doze constraints.
 */
object TitanWorkManagerHelper {

  private const val TAG = "TitanWorkManagerHelper"

  /**
   * Initializes all background automation periodic workers according to OS guidelines.
   * WorkManager enforces a minimum periodic interval of 15 minutes.
   */
  fun scheduleAllPeriodicTasks(
    context: Context,
    jobDiscoveryIntervalMinutes: Long = 60L,
    linkedInSyncIntervalHours: Long = 6L,
    platformSyncIntervalMinutes: Long = 60L
  ) {
    try {
      val workManager = WorkManager.getInstance(context.applicationContext)

      // Standard network constraint
      val networkConstraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

      // 1. Job Discovery Periodic Sync
      val effectiveJobInterval = jobDiscoveryIntervalMinutes.coerceAtLeast(15L)
      val jobDiscoveryRequest = PeriodicWorkRequestBuilder<JobDiscoveryWorker>(
        effectiveJobInterval, TimeUnit.MINUTES
      )
        .setConstraints(networkConstraint)
        .build()

      workManager.enqueueUniquePeriodicWork(
        JobDiscoveryWorker.UNIQUE_WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        jobDiscoveryRequest
      )
      Log.i(TAG, "Enqueued periodic JobDiscoveryWorker (interval: ${effectiveJobInterval}m)")

      // 2. LinkedIn Professional Profile Sync
      val effectiveLinkedInHours = linkedInSyncIntervalHours.coerceAtLeast(1L)
      val linkedInRequest = PeriodicWorkRequestBuilder<LinkedInSyncWorker>(
        effectiveLinkedInHours, TimeUnit.HOURS
      )
        .setConstraints(networkConstraint)
        .build()

      workManager.enqueueUniquePeriodicWork(
        LinkedInSyncWorker.UNIQUE_WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        linkedInRequest
      )
      Log.i(TAG, "Enqueued periodic LinkedInSyncWorker (interval: ${effectiveLinkedInHours}h)")

      // 3. Platform Job Sync (Indeed, Naukri, LinkedIn Jobs)
      val effectivePlatformInterval = platformSyncIntervalMinutes.coerceAtLeast(15L)
      val platformSyncRequest = PeriodicWorkRequestBuilder<PlatformJobSyncWorker>(
        effectivePlatformInterval, TimeUnit.MINUTES
      )
        .setConstraints(networkConstraint)
        .build()

      workManager.enqueueUniquePeriodicWork(
        PlatformJobSyncWorker.UNIQUE_WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        platformSyncRequest
      )
      Log.i(TAG, "Enqueued periodic PlatformJobSyncWorker (interval: ${effectivePlatformInterval}m)")

      // 4. Dark Store 07:00 AM SLA & Operational Monitor
      val darkStoreSlaRequest = PeriodicWorkRequestBuilder<DarkStoreSlaWorker>(
        4L, TimeUnit.HOURS
      )
        .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
        .build()

      workManager.enqueueUniquePeriodicWork(
        DarkStoreSlaWorker.UNIQUE_WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        darkStoreSlaRequest
      )
      Log.i(TAG, "Enqueued periodic DarkStoreSlaWorker (interval: 4h)")

      // 5. Batch Application Status Sync
      schedulePeriodicBatchJobSync(context)
    } catch (e: Throwable) {
      Log.w(TAG, "WorkManager initialization or scheduling skipped: ${e.message}")
    }
  }

  fun schedulePeriodicBatchJobSync(context: Context) {
    try {
      val workManager = WorkManager.getInstance(context.applicationContext)
      val networkConstraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
      val batchJobRequest = PeriodicWorkRequestBuilder<BatchApplicationSyncWorker>(
        15L, TimeUnit.MINUTES
      )
        .setConstraints(networkConstraint)
        .build()

      workManager.enqueueUniquePeriodicWork(
        BatchApplicationSyncWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        batchJobRequest
      )
      Log.i(TAG, "Enqueued periodic BatchApplicationSyncWorker (interval: 15m)")
    } catch (e: Throwable) {
      Log.w(TAG, "BatchApplicationSyncWorker scheduling skipped: ${e.message}")
    }
  }

  fun triggerImmediateBatchJobSync(context: Context) {
    try {
      val workManager = WorkManager.getInstance(context.applicationContext)
      val request = androidx.work.OneTimeWorkRequestBuilder<BatchApplicationSyncWorker>().build()
      workManager.enqueue(request)
      Log.i(TAG, "Triggered one-time BatchApplicationSyncWorker execution")
    } catch (e: Throwable) {
      Log.w(TAG, "triggerImmediateBatchJobSync skipped: ${e.message}")
    }
  }

  fun triggerImmediateDarkStoreSlaCheck(context: Context) {
    try {
      val workManager = WorkManager.getInstance(context.applicationContext)
      val request = androidx.work.OneTimeWorkRequestBuilder<DarkStoreSlaWorker>().build()
      workManager.enqueue(request)
      Log.i(TAG, "Triggered one-time DarkStoreSlaWorker execution")
    } catch (e: Throwable) {
      Log.w(TAG, "triggerImmediateDarkStoreSlaCheck skipped: ${e.message}")
    }
  }

  fun cancelAllPeriodicTasks(context: Context) {
    try {
      val workManager = WorkManager.getInstance(context.applicationContext)
      workManager.cancelUniqueWork(JobDiscoveryWorker.UNIQUE_WORK_NAME)
      workManager.cancelUniqueWork(LinkedInSyncWorker.UNIQUE_WORK_NAME)
      workManager.cancelUniqueWork(PlatformJobSyncWorker.UNIQUE_WORK_NAME)
      workManager.cancelUniqueWork(DarkStoreSlaWorker.UNIQUE_WORK_NAME)
      workManager.cancelUniqueWork(BatchApplicationSyncWorker.WORK_NAME)
      Log.i(TAG, "Cancelled all periodic background synchronization workers")
    } catch (e: Throwable) {
      Log.w(TAG, "cancelAllPeriodicTasks skipped: ${e.message}")
    }
  }
}
