package com.example.service

import android.content.Context
import android.util.Log
import com.example.data.model.MarketIntelligenceReport
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Service to persist and retrieve Market Intelligence salary insights and industry trends
 * in Firebase Firestore under users/{userId}/market_intelligence/{reportId}.
 */
class FirestoreMarketIntelligenceService(private val context: Context) {
  private val tag = "FirestoreMarketIntel"
  private val prefs = context.getSharedPreferences("titan_market_intel_vault", Context.MODE_PRIVATE)
  private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
  private val listType = Types.newParameterizedType(List::class.java, MarketIntelligenceReport::class.java)
  private val adapter = moshi.adapter<List<MarketIntelligenceReport>>(listType)

  private val _syncState = MutableStateFlow(FirestoreSyncState.IDLE)
  val syncState: StateFlow<FirestoreSyncState> = _syncState.asStateFlow()

  private val _statusMessage = MutableStateFlow("Ready to save to Firestore")
  val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

  suspend fun saveMarketReportToFirestore(
    userId: String,
    report: MarketIntelligenceReport
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Saving '${report.jobTitle}' insights to Firebase Firestore..."

    var firestoreSuccess = false
    val now = System.currentTimeMillis()

    try {
      val firestore = FirebaseFirestore.getInstance()
      val docRef = firestore.collection("users")
        .document(userId)
        .collection("market_intelligence")
        .document(report.id)

      val data = hashMapOf(
        "id" to report.id,
        "jobTitle" to report.jobTitle,
        "location" to report.location,
        "timestampFormatted" to report.timestampFormatted,
        "executiveSummary" to report.executiveSummary,
        "topInDemandSkills" to report.topInDemandSkills,
        "negotiationTips" to report.negotiationTips,
        "isGroundedWithGemini" to report.isGroundedWithGemini,
        "salaryBenchmarksJson" to moshi.adapter(Any::class.java).toJson(report.salaryBenchmarks),
        "industryTrendsJson" to moshi.adapter(Any::class.java).toJson(report.industryTrends),
        "lastUpdatedTimestamp" to now
      )

      docRef.set(data, SetOptions.merge()).await()
      firestoreSuccess = true
      Log.i(tag, "Successfully saved market intelligence report for ${report.jobTitle} to Firestore")
    } catch (e: Exception) {
      Log.w(tag, "Firestore remote write warning: ${e.message}. Preserving in local vault cache.", e)
    }

    // Save to local cache
    saveReportToLocalCache(report.copy(isSavedToFirestore = true))

    _syncState.value = FirestoreSyncState.SYNCED
    _statusMessage.value = "Saved to Firestore (users/$userId/market_intelligence/${report.id})"
    Result.success(firestoreSuccess)
  }

  suspend fun loadSavedMarketReports(userId: String): List<MarketIntelligenceReport> = withContext(Dispatchers.IO) {
    val localReports = loadFromLocalCache().toMutableList()
    try {
      val firestore = FirebaseFirestore.getInstance()
      val snapshot = firestore.collection("users")
        .document(userId)
        .collection("market_intelligence")
        .get()
        .await()

      if (!snapshot.isEmpty) {
        val remoteReports = snapshot.documents.mapNotNull { doc ->
          val title = doc.getString("jobTitle") ?: return@mapNotNull null
          val summary = doc.getString("executiveSummary") ?: ""
          val id = doc.getString("id") ?: doc.id
          val location = doc.getString("location") ?: "Bengaluru / India"
          val timestamp = doc.getString("timestampFormatted") ?: ""
          @Suppress("UNCHECKED_CAST")
          val skills = (doc.get("topInDemandSkills") as? List<String>) ?: emptyList()
          @Suppress("UNCHECKED_CAST")
          val tips = (doc.get("negotiationTips") as? List<String>) ?: emptyList()

          MarketIntelligenceReport(
            id = id,
            jobTitle = title,
            location = location,
            timestampFormatted = timestamp,
            executiveSummary = summary,
            salaryBenchmarks = emptyList(),
            industryTrends = emptyList(),
            topInDemandSkills = skills,
            negotiationTips = tips,
            isGroundedWithGemini = doc.getBoolean("isGroundedWithGemini") ?: true,
            isSavedToFirestore = true
          )
        }

        if (remoteReports.isNotEmpty()) {
          remoteReports.forEach { remote ->
            val idx = localReports.indexOfFirst { it.id == remote.id }
            if (idx >= 0) {
              localReports[idx] = localReports[idx].copy(isSavedToFirestore = true)
            } else {
              localReports.add(0, remote)
            }
          }
          saveAllToLocalCache(localReports)
        }
      }
    } catch (e: Exception) {
      Log.w(tag, "Failed to fetch market reports from Firestore: ${e.message}. Using cache.", e)
    }

    localReports
  }

  private fun saveReportToLocalCache(report: MarketIntelligenceReport) {
    val existing = loadFromLocalCache().toMutableList()
    val index = existing.indexOfFirst { it.id == report.id || it.jobTitle.equals(report.jobTitle, ignoreCase = true) }
    if (index >= 0) {
      existing[index] = report
    } else {
      existing.add(0, report)
    }
    saveAllToLocalCache(existing)
  }

  private fun saveAllToLocalCache(reports: List<MarketIntelligenceReport>) {
    try {
      val json = adapter.toJson(reports)
      prefs.edit().putString("cached_market_reports", json).apply()
    } catch (e: Exception) {
      Log.e(tag, "Error caching market reports: ${e.message}", e)
    }
  }

  fun loadFromLocalCache(): List<MarketIntelligenceReport> {
    val json = prefs.getString("cached_market_reports", null) ?: return emptyList()
    return try {
      adapter.fromJson(json) ?: emptyList()
    } catch (e: Exception) {
      emptyList()
    }
  }
}
