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
  }

  fun cancelAllPeriodicTasks(context: Context) {
    val workManager = WorkManager.getInstance(context.applicationContext)
    workManager.cancelUniqueWork(JobDiscoveryWorker.UNIQUE_WORK_NAME)
    workManager.cancelUniqueWork(LinkedInSyncWorker.UNIQUE_WORK_NAME)
    workManager.cancelUniqueWork(PlatformJobSyncWorker.UNIQUE_WORK_NAME)
    Log.i(TAG, "Cancelled all periodic background synchronization workers")
  }
}
