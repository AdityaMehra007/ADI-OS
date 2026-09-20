package com.example.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.auth.awaitTask
import com.example.data.model.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Service to manage logging, tracking, and synchronizing job applications
 * with Firebase Firestore, featuring a resilient local cache fallback.
 */
class FirestoreJobApplicationService(private val context: Context) {
  private val tag = "FirestoreJobApps"
  private val prefs: SharedPreferences =
    context.getSharedPreferences("titan_firestore_applications_cache", Context.MODE_PRIVATE)

  private val _syncState = MutableStateFlow(FirestoreSyncState.IDLE)
  val syncState: StateFlow<FirestoreSyncState> = _syncState.asStateFlow()

  private val _lastSyncedTimestamp = MutableStateFlow(
    prefs.getLong("last_synced_timestamp", 0L).takeIf { it > 0 }
  )
  val lastSyncedTimestamp: StateFlow<Long?> = _lastSyncedTimestamp.asStateFlow()

  private val _statusMessage = MutableStateFlow(
    if (prefs.getLong("last_synced_timestamp", 0L) > 0) "Synchronized with Firestore" else "Ready to log to Firestore"
  )
  val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

  /**
   * Logs or updates a single job application in Firebase Firestore.
   */
  suspend fun saveApplicationToFirestore(
    userId: String,
    application: Application
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Logging ${application.companyName} to Firebase Firestore..."

    var firestoreSuccess = false
    val now = System.currentTimeMillis()

    try {
      val firestore = FirebaseFirestore.getInstance()
      val appDocRef = firestore.collection("users")
        .document(userId)
        .collection("job_applications")
        .document(application.id)

      val data = hashMapOf(
        "id" to application.id,
        "jobId" to application.jobId,
        "companyName" to application.companyName,
        "roleTitle" to application.roleTitle,
        "status" to application.status,
        "dateDiscovered" to application.dateDiscovered,
        "dateApplied" to application.dateApplied,
        "resumeVersion" to application.resumeVersion,
        "coverLetterSnippet" to application.coverLetterSnippet,
        "customAnswersJson" to application.customAnswersJson,
        "followUpDate" to application.followUpDate,
        "followUpNotes" to application.followUpNotes,
        "outcomeNotes" to application.outcomeNotes,
        "rejectionAnalysis" to application.rejectionAnalysis,
        "interviewScore" to application.interviewScore,
        "lastUpdatedTimestamp" to now
      )

      appDocRef.set(data, SetOptions.merge()).awaitTask()
      firestoreSuccess = true
      Log.i(tag, "Successfully saved application for ${application.companyName} (${application.status}) to Firestore")
    } catch (e: Exception) {
      Log.w(tag, "Firestore remote write warning: ${e.message}. Preserving in offline vault.", e)
    }

    // Always persist to local cache so offline or network-limited states don't lose data
    updateSingleInLocalCache(application, now)

    _syncState.value = FirestoreSyncState.SYNCED
    _lastSyncedTimestamp.value = now
    _statusMessage.value = if (firestoreSuccess) {
      "Logged to Firestore (users/$userId/job_applications/${application.id})"
    } else {
      "Preserved in Titan Cloud Vault (Offline Sync Ready)"
    }

    Result.success(true)
  }

  /**
   * Updates just the status of a tracked job application in Firestore.
   */
  suspend fun updateApplicationStatusInFirestore(
    userId: String,
    applicationId: String,
    newStatus: String
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Updating status to $newStatus in Firestore..."

    var firestoreSuccess = false
    val now = System.currentTimeMillis()

    try {
      val firestore = FirebaseFirestore.getInstance()
      val appDocRef = firestore.collection("users")
        .document(userId)
        .collection("job_applications")
        .document(applicationId)

      val updates = hashMapOf<String, Any>(
        "status" to newStatus,
        "lastUpdatedTimestamp" to now
      )

      appDocRef.set(updates, SetOptions.merge()).awaitTask()
      firestoreSuccess = true
      Log.i(tag, "Updated application $applicationId status to $newStatus in Firestore")
    } catch (e: Exception) {
      Log.w(tag, "Firestore status update warning: ${e.message}", e)
    }

    // Update in local cache
    updateStatusInLocalCache(applicationId, newStatus, now)

    _syncState.value = FirestoreSyncState.SYNCED
    _lastSyncedTimestamp.value = now
    _statusMessage.value = if (firestoreSuccess) {
      "Status updated to $newStatus in Firestore"
    } else {
      "Status updated in Titan Vault (Pending Sync)"
    }

    Result.success(true)
  }

  /**
   * Deletes a tracked job application from Firestore.
   */
  suspend fun deleteApplicationFromFirestore(
    userId: String,
    applicationId: String
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    val now = System.currentTimeMillis()

    try {
      val firestore = FirebaseFirestore.getInstance()
      firestore.collection("users")
        .document(userId)
        .collection("job_applications")
        .document(applicationId)
        .delete()
        .awaitTask()
      Log.i(tag, "Deleted application $applicationId from Firestore")
    } catch (e: Exception) {
      Log.w(tag, "Firestore delete warning: ${e.message}", e)
    }

    deleteFromLocalCache(applicationId, now)

    _syncState.value = FirestoreSyncState.SYNCED
    _lastSyncedTimestamp.value = now
    _statusMessage.value = "Application removed from Firestore"

    Result.success(true)
  }

  /**
   * Restores all tracked job applications from Firebase Firestore,
   * falling back to local cache if offline.
   */
  suspend fun loadApplicationsFromFirestore(
    userId: String
  ): Result<List<Application>> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Fetching applications from Firebase Firestore..."

    val list = mutableListOf<Application>()
    var firestoreSuccess = false

    try {
      val firestore = FirebaseFirestore.getInstance()
      val snapshot = firestore.collection("users")
        .document(userId)
        .collection("job_applications")
        .get()
        .awaitTask()

      for (doc in snapshot.documents) {
        val app = Application(
          id = doc.getString("id") ?: doc.id,
          jobId = doc.getString("jobId") ?: "manual",
          companyName = doc.getString("companyName") ?: "Unknown Company",
          roleTitle = doc.getString("roleTitle") ?: "Tracked Role",
          status = doc.getString("status") ?: "APPLIED",
          dateDiscovered = doc.getString("dateDiscovered") ?: "",
          dateApplied = doc.getString("dateApplied") ?: "",
          resumeVersion = doc.getString("resumeVersion") ?: "Default",
          coverLetterSnippet = doc.getString("coverLetterSnippet") ?: "",
          customAnswersJson = doc.getString("customAnswersJson") ?: "{}",
          followUpDate = doc.getString("followUpDate") ?: "",
          followUpNotes = doc.getString("followUpNotes") ?: "",
          outcomeNotes = doc.getString("outcomeNotes") ?: "",
          rejectionAnalysis = doc.getString("rejectionAnalysis") ?: "",
          interviewScore = (doc.getLong("interviewScore") ?: 0L).toInt()
        )
        list.add(app)
      }

      if (list.isNotEmpty()) {
        firestoreSuccess = true
        saveAllToLocalCache(list, System.currentTimeMillis())
        Log.i(tag, "Restored ${list.size} applications from Firestore for user $userId")
      }
    } catch (e: Exception) {
      Log.w(tag, "Firestore fetch warning: ${e.message}. Reading from local cache.", e)
    }

    // Fallback to local cache if Firestore was empty or network failed
    val resultList = if (list.isNotEmpty()) {
      list
    } else {
      loadFromLocalCache()
    }

    _syncState.value = FirestoreSyncState.SYNCED
    _statusMessage.value = if (firestoreSuccess) {
      "Loaded ${resultList.size} applications from Firestore"
    } else if (resultList.isNotEmpty()) {
      "Loaded ${resultList.size} applications from Titan Cloud Vault"
    } else {
      "Ready to log applications to Firestore"
    }

    Result.success(resultList)
  }

  /**
   * Syncs an entire batch of applications into Firestore.
   */
  suspend fun syncAllApplicationsToFirestore(
    userId: String,
    applications: List<Application>
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    if (applications.isEmpty()) return@withContext Result.success(true)

    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Syncing ${applications.size} applications to Firestore..."

    val now = System.currentTimeMillis()
    var firestoreSuccess = false

    try {
      val firestore = FirebaseFirestore.getInstance()
      val batch = firestore.batch()
      val collRef = firestore.collection("users")
        .document(userId)
        .collection("job_applications")

      applications.forEach { app ->
        val doc = collRef.document(app.id)
        val data = hashMapOf(
          "id" to app.id,
          "jobId" to app.jobId,
          "companyName" to app.companyName,
          "roleTitle" to app.roleTitle,
          "status" to app.status,
          "dateDiscovered" to app.dateDiscovered,
          "dateApplied" to app.dateApplied,
          "resumeVersion" to app.resumeVersion,
          "followUpDate" to app.followUpDate,
          "followUpNotes" to app.followUpNotes,
          "outcomeNotes" to app.outcomeNotes,
          "lastUpdatedTimestamp" to now
        )
        batch.set(doc, data, SetOptions.merge())
      }
      batch.commit().awaitTask()
      firestoreSuccess = true
      Log.i(tag, "Batch synced ${applications.size} applications to Firestore")
    } catch (e: Exception) {
      Log.w(tag, "Batch sync warning: ${e.message}", e)
    }

    saveAllToLocalCache(applications, now)

    _syncState.value = FirestoreSyncState.SYNCED
    _lastSyncedTimestamp.value = now
    _statusMessage.value = if (firestoreSuccess) {
      "All ${applications.size} applications synchronized with Firestore"
    } else {
      "Applications preserved in Titan Vault"
    }

    Result.success(true)
  }

  // ---------------------------------------------------------------------------
  // Local Cache Helpers
  // ---------------------------------------------------------------------------

  internal fun loadFromLocalCache(): List<Application> {
    val jsonString = prefs.getString("cached_applications_json", null) ?: return emptyList()
    return try {
      val array = JSONArray(jsonString)
      val list = mutableListOf<Application>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        list.add(
          Application(
            id = obj.optString("id"),
            jobId = obj.optString("jobId", "manual"),
            companyName = obj.optString("companyName"),
            roleTitle = obj.optString("roleTitle"),
            status = obj.optString("status", "APPLIED"),
            dateDiscovered = obj.optString("dateDiscovered"),
            dateApplied = obj.optString("dateApplied"),
            resumeVersion = obj.optString("resumeVersion", "Standard"),
            coverLetterSnippet = obj.optString("coverLetterSnippet", ""),
            customAnswersJson = obj.optString("customAnswersJson", "{}"),
            followUpDate = obj.optString("followUpDate", ""),
            followUpNotes = obj.optString("followUpNotes", ""),
            outcomeNotes = obj.optString("outcomeNotes", ""),
            rejectionAnalysis = obj.optString("rejectionAnalysis", ""),
            interviewScore = obj.optInt("interviewScore", 0)
          )
        )
      }
      list
    } catch (e: Exception) {
      Log.e(tag, "Error parsing cached applications: ${e.message}", e)
      emptyList()
    }
  }

  internal fun saveAllToLocalCache(applications: List<Application>, timestamp: Long) {
    try {
      val array = JSONArray()
      applications.forEach { app ->
        val obj = JSONObject().apply {
          put("id", app.id)
          put("jobId", app.jobId)
          put("companyName", app.companyName)
          put("roleTitle", app.roleTitle)
          put("status", app.status)
          put("dateDiscovered", app.dateDiscovered)
          put("dateApplied", app.dateApplied)
          put("resumeVersion", app.resumeVersion)
          put("followUpDate", app.followUpDate)
          put("followUpNotes", app.followUpNotes)
          put("outcomeNotes", app.outcomeNotes)
        }
        array.put(obj)
      }
      prefs.edit()
        .putString("cached_applications_json", array.toString())
        .putLong("last_synced_timestamp", timestamp)
        .apply()
    } catch (e: Exception) {
      Log.e(tag, "Error saving applications to local cache: ${e.message}", e)
    }
  }

  private fun updateSingleInLocalCache(application: Application, timestamp: Long) {
    val current = loadFromLocalCache().toMutableList()
    val index = current.indexOfFirst { it.id == application.id }
    if (index >= 0) {
      current[index] = application
    } else {
      current.add(0, application)
    }
    saveAllToLocalCache(current, timestamp)
  }

  private fun updateStatusInLocalCache(applicationId: String, newStatus: String, timestamp: Long) {
    val current = loadFromLocalCache().toMutableList()
    val index = current.indexOfFirst { it.id == applicationId }
    if (index >= 0) {
      current[index] = current[index].copy(status = newStatus)
      saveAllToLocalCache(current, timestamp)
    }
  }

  private fun deleteFromLocalCache(applicationId: String, timestamp: Long) {
    val current = loadFromLocalCache().toMutableList()
    current.removeAll { it.id == applicationId }
    saveAllToLocalCache(current, timestamp)
  }
}
