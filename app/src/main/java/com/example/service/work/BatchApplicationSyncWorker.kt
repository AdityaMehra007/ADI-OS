package com.example.service.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.data.TitanDatabase
import com.example.data.model.CareerTelemetry
import com.example.data.model.TelemetryMetricType
import com.example.notification.TitanNotificationManager
import com.example.service.JobPlatformSyncEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Background WorkManager Worker responsible for periodically synchronizing application statuses
 * across multiple target companies (Zepto, Razorpay, Blinkit, Instawork, Swiggy, etc.).
 *
 * Ensures the candidate dashboard and career telemetry ledger stay continuously updated
 * without requiring foreground execution.
 */
class BatchApplicationSyncWorker(
  appContext: Context,
  workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

  companion object {
    const val TAG = "BatchApplicationSyncWorker"
    const val WORK_NAME = "titan_batch_application_sync"
    const val KEY_SYNCED_COUNT = "synced_applications_count"
    const val KEY_UPDATED_COUNT = "updated_applications_count"
    const val KEY_SYNC_TIMESTAMP = "last_sync_timestamp"
  }

  override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
    Log.i(TAG, "Executing periodic background batch application status sync cycle...")
    val scope = CoroutineScope(Dispatchers.IO)
    val database = TitanDatabase.getDatabase(applicationContext, scope)
    val notificationManager = TitanNotificationManager.getInstance(applicationContext)

    try {
      // 1. Fetch current tracked applications across all companies
      val currentApplications = database.applicationDao().getAllApplications().first()
      var updatedCount = 0
      val syncedCount = currentApplications.size
      val targetCompaniesTracked = currentApplications.map { it.companyName }.distinct()

      Log.i(TAG, "Polled ${currentApplications.size} tracked applications across ${targetCompaniesTracked.size} target companies: $targetCompaniesTracked")

      // 2. Trigger multi-platform synchronization through JobPlatformSyncEngine
      val syncEngine = JobPlatformSyncEngine.getInstance(applicationContext)
      val syncResult = syncEngine.executeSyncCycle("WORK_MANAGER_BATCH")

      // 3. Inspect status progression across target companies
      for (app in currentApplications) {
        // If an application has updated stage in platform transitions, persist and log telemetry
        val matchingTransition = syncResult.statusTransitions.find { 
          it.applicationId == app.id || it.companyName.equals(app.companyName, ignoreCase = true) 
        }
        
        if (matchingTransition != null && matchingTransition.currentStatus != app.status) {
          val previousStatus = app.status
          val newStatus = matchingTransition.currentStatus
          
          // Update application in Room
          database.applicationDao().updateApplicationStatus(app.id, newStatus)
          updatedCount++

          // Record structured Career Telemetry in Room
          val telemetry = CareerTelemetry(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            metricType = TelemetryMetricType.APPLICATION_STATUS_CHANGE.name,
            companyName = app.companyName,
            roleTitle = app.roleTitle,
            previousStatus = previousStatus,
            newStatus = newStatus,
            details = "Batch Job Processor detected status change from $previousStatus to $newStatus across ${matchingTransition.platform.displayName}.",
            score = app.interviewScore,
            source = "WORK_MANAGER_BATCH"
          )
          database.careerTelemetryDao().insertTelemetry(telemetry)

          // Dispatch high-signal executive notification
          notificationManager.notifyApplicationStatus(
            companyName = app.companyName,
            roleTitle = app.roleTitle,
            newStatus = newStatus,
            notes = "Batch sync detected application progression to $newStatus."
          )
        }
      }

      // 4. Record batch telemetry summary
      val batchCompletionTelemetry = CareerTelemetry(
        id = UUID.randomUUID().toString(),
        timestamp = System.currentTimeMillis(),
        metricType = TelemetryMetricType.PIPELINE_VELOCITY_UPDATE.name,
        companyName = "Batch Processor",
        roleTitle = "All Targets",
        previousStatus = "RUNNING",
        newStatus = "COMPLETED",
        details = "Synchronized $syncedCount applications across ${targetCompaniesTracked.size} target companies. State deltas detected: $updatedCount.",
        score = 100,
        source = "WORK_MANAGER_BATCH"
      )
      database.careerTelemetryDao().insertTelemetry(batchCompletionTelemetry)

      Log.i(TAG, "Batch job sync cycle completed successfully. Synced: $syncedCount, Updated: $updatedCount")

      val outputData = workDataOf(
        KEY_SYNCED_COUNT to syncedCount,
        KEY_UPDATED_COUNT to updatedCount,
        KEY_SYNC_TIMESTAMP to System.currentTimeMillis()
      )

      Result.success(outputData)
    } catch (e: Exception) {
      Log.e(TAG, "Error executing background batch application sync: ${e.message}", e)
      if (runAttemptCount < 3) {
        Result.retry()
      } else {
        Result.failure(workDataOf("error" to (e.message ?: "Unknown sync error")))
      }
    }
  }
}
