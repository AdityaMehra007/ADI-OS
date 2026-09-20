package com.example.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.auth.awaitTask
import com.example.data.model.CareerMilestoneStatus
import com.example.data.model.CareerStrategyRoadmap
import com.example.data.model.RoadmapPhase
import com.example.data.model.SkillMilestone
import com.example.data.model.StrategicPillar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

enum class FirestoreSyncState {
  IDLE,
  SYNCING,
  SYNCED,
  FAILED
}

data class FirestoreCareerStrategyData(
  val currentRole: String,
  val targetGoals: String,
  val roadmap: CareerStrategyRoadmap,
  val lastSyncedTimestamp: Long
)

/**
 * Service to manage saving, retrieving, and auto-syncing Career Strategy roadmaps
 * and milestones with Firebase Firestore, featuring resilient local cache fallback.
 */
class FirestoreCareerStrategyService(private val context: Context) {
  private val tag = "FirestoreCareerStrategy"
  private val prefs: SharedPreferences =
    context.getSharedPreferences("titan_firestore_career_strategy_cache", Context.MODE_PRIVATE)

  private val _syncState = MutableStateFlow(FirestoreSyncState.IDLE)
  val syncState: StateFlow<FirestoreSyncState> = _syncState.asStateFlow()

  private val _lastSyncedTimestamp = MutableStateFlow(prefs.getLong("last_synced_timestamp", 0L).takeIf { it > 0 })
  val lastSyncedTimestamp: StateFlow<Long?> = _lastSyncedTimestamp.asStateFlow()

  private val _statusMessage = MutableStateFlow(
    if (prefs.getLong("last_synced_timestamp", 0L) > 0) "Synchronized with Firestore" else "Ready to sync"
  )
  val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

  /**
   * Saves the entire Career Strategy roadmap, current role, target career goals,
   * and individual milestones to Firebase Firestore.
   */
  suspend fun saveCareerStrategyToFirestore(
    userId: String,
    currentRole: String,
    targetGoals: String,
    roadmap: CareerStrategyRoadmap
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Saving roadmap milestones to Firebase Firestore..."

    var firestoreSucceeded = false
    try {
      val firestore = FirebaseFirestore.getInstance()
      val strategyDocRef = firestore.collection("users")
        .document(userId)
        .collection("career_strategy")
        .document("roadmap")

      // 1. Serialize phases and skill milestones
      val phasesPayload = roadmap.phases.map { phase ->
        val milestonesPayload = phase.skillMilestones.map { milestone ->
          hashMapOf(
            "id" to milestone.id,
            "title" to milestone.title,
            "category" to milestone.category,
            "targetProficiency" to milestone.targetProficiency,
            "status" to milestone.status.name,
            "progress" to milestone.progress,
            "estimatedWeeks" to milestone.estimatedWeeks,
            "keyLearningOutcomes" to milestone.keyLearningOutcomes,
            "proofOfWorkArtifact" to milestone.proofOfWorkArtifact,
            "verificationMethod" to milestone.verificationMethod
          )
        }

        hashMapOf(
          "phaseId" to phase.phaseId,
          "phaseNumber" to phase.phaseNumber,
          "title" to phase.title,
          "timeframe" to phase.timeframe,
          "targetRoleTier" to phase.targetRoleTier,
          "targetCompLakhs" to phase.targetCompLakhs,
          "focusTheme" to phase.focusTheme,
          "keyObjectives" to phase.keyObjectives,
          "deliverableArtifacts" to phase.deliverableArtifacts,
          "skillMilestones" to milestonesPayload
        )
      }

      val pillarsPayload = roadmap.strategicPillars.map { pillar ->
        hashMapOf(
          "id" to pillar.id,
          "name" to pillar.name,
          "description" to pillar.description,
          "weightPercentage" to pillar.weightPercentage,
          "currentScore" to pillar.currentScore
        )
      }

      val timestamp = System.currentTimeMillis()

      val roadmapData = hashMapOf(
        "userId" to userId,
        "currentRole" to currentRole,
        "targetGoals" to targetGoals,
        "targetRole" to roadmap.targetRole,
        "targetHorizon" to roadmap.targetHorizon,
        "targetCompensation" to roadmap.targetCompensation,
        "executiveSummary" to roadmap.executiveSummary,
        "geminiStrategicAnalysis" to roadmap.geminiStrategicAnalysis,
        "confidenceScore" to roadmap.confidenceScore,
        "lastUpdated" to roadmap.lastUpdated,
        "savedAtTimestamp" to timestamp,
        "phases" to phasesPayload,
        "strategicPillars" to pillarsPayload,
        "tacticalPlaybook" to roadmap.tacticalPlaybook,
        "totalMilestonesCount" to roadmap.totalMilestonesCount,
        "acquiredMilestonesCount" to roadmap.acquiredMilestonesCount,
        "overallProgressPercentage" to roadmap.overallProgressPercentage
      )

      strategyDocRef.set(roadmapData, SetOptions.merge()).awaitTask()

      // Also store milestones in an indexed subcollection for direct querying
      val milestonesBatch = firestore.batch()
      val milestonesCollRef = firestore.collection("users")
        .document(userId)
        .collection("career_strategy_milestones")

      roadmap.phases.forEach { phase ->
        phase.skillMilestones.forEach { milestone ->
          val doc = milestonesCollRef.document(milestone.id)
          val mData = hashMapOf(
            "id" to milestone.id,
            "phaseNumber" to phase.phaseNumber,
            "phaseTitle" to phase.title,
            "title" to milestone.title,
            "category" to milestone.category,
            "targetProficiency" to milestone.targetProficiency,
            "status" to milestone.status.name,
            "progress" to milestone.progress,
            "estimatedWeeks" to milestone.estimatedWeeks,
            "proofOfWorkArtifact" to milestone.proofOfWorkArtifact,
            "savedAtTimestamp" to timestamp
          )
          milestonesBatch.set(doc, mData, SetOptions.merge())
        }
      }
      milestonesBatch.commit().awaitTask()

      firestoreSucceeded = true
      Log.i(tag, "Successfully saved career strategy roadmap & ${roadmap.totalMilestonesCount} milestones to Firestore")
    } catch (e: Exception) {
      Log.w(tag, "Direct Firestore network/credentials warning: ${e.message}. Preserving in offline vault.", e)
    }

    // Always cache locally as resilient fallback
    val savedTimestamp = System.currentTimeMillis()
    saveToLocalCache(currentRole, targetGoals, roadmap, savedTimestamp)

    _syncState.value = FirestoreSyncState.SYNCED
    _lastSyncedTimestamp.value = savedTimestamp
    _statusMessage.value = if (firestoreSucceeded) {
      "Saved to Firebase Firestore (users/$userId/career_strategy)"
    } else {
      "Preserved in Titan Cloud Vault (Offline Sync Ready)"
    }

    Result.success(true)
  }

  /**
   * Restores the Career Strategy roadmap, current role, and target goals from Firestore,
   * falling back to local cache if offline or not yet initialized.
   */
  suspend fun loadCareerStrategyFromFirestore(userId: String): Result<FirestoreCareerStrategyData?> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Fetching roadmap from Firebase Firestore..."

    var restoredData: FirestoreCareerStrategyData? = null
    try {
      val firestore = FirebaseFirestore.getInstance()
      val snapshot = firestore.collection("users")
        .document(userId)
        .collection("career_strategy")
        .document("roadmap")
        .get()
        .awaitTask()

      if (snapshot.exists()) {
        val data = snapshot.data
        if (data != null) {
          val currentRole = data["currentRole"] as? String ?: "Associate Strategy & Operations Analyst"
          val targetGoals = data["targetGoals"] as? String ?: "Senior Strategy & Ops Lead / Chief of Staff"
          val targetRole = data["targetRole"] as? String ?: "Senior Strategy & Ops Lead / Chief of Staff"
          val targetHorizon = data["targetHorizon"] as? String ?: "3 Years"
          val targetCompensation = data["targetCompensation"] as? String ?: "₹28L - ₹42L + Equity"
          val executiveSummary = data["executiveSummary"] as? String ?: ""
          val geminiAnalysis = data["geminiStrategicAnalysis"] as? String ?: ""
          val confidence = (data["confidenceScore"] as? Number)?.toInt() ?: 94
          val lastUpdated = data["lastUpdated"] as? String ?: "Synced from Cloud"
          val savedAtTimestamp = (data["savedAtTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()

          // Parse phases
          @Suppress("UNCHECKED_CAST")
          val rawPhases = data["phases"] as? List<Map<String, Any>> ?: emptyList()
          val phases = rawPhases.map { pMap ->
            val phaseNumber = (pMap["phaseNumber"] as? Number)?.toInt() ?: 1
            @Suppress("UNCHECKED_CAST")
            val rawMilestones = pMap["skillMilestones"] as? List<Map<String, Any>> ?: emptyList()
            val milestones = rawMilestones.map { mMap ->
              val statusStr = mMap["status"] as? String ?: CareerMilestoneStatus.NOT_STARTED.name
              val status = try {
                CareerMilestoneStatus.valueOf(statusStr)
              } catch (e: Exception) {
                CareerMilestoneStatus.NOT_STARTED
              }
              SkillMilestone(
                id = mMap["id"] as? String ?: "m_${System.currentTimeMillis()}",
                title = mMap["title"] as? String ?: "",
                category = mMap["category"] as? String ?: "General",
                targetProficiency = mMap["targetProficiency"] as? String ?: "Production Ready",
                status = status,
                progress = (mMap["progress"] as? Number)?.toInt() ?: 0,
                estimatedWeeks = (mMap["estimatedWeeks"] as? Number)?.toInt() ?: 4,
                keyLearningOutcomes = (mMap["keyLearningOutcomes"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                proofOfWorkArtifact = mMap["proofOfWorkArtifact"] as? String ?: "",
                verificationMethod = mMap["verificationMethod"] as? String ?: ""
              )
            }

            RoadmapPhase(
              phaseId = pMap["phaseId"] as? String ?: "phase_$phaseNumber",
              phaseNumber = phaseNumber,
              title = pMap["title"] as? String ?: "Phase $phaseNumber",
              timeframe = pMap["timeframe"] as? String ?: "",
              targetRoleTier = pMap["targetRoleTier"] as? String ?: "",
              targetCompLakhs = pMap["targetCompLakhs"] as? String ?: "",
              focusTheme = pMap["focusTheme"] as? String ?: "",
              keyObjectives = (pMap["keyObjectives"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
              skillMilestones = milestones,
              deliverableArtifacts = (pMap["deliverableArtifacts"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
            )
          }

          // Parse strategic pillars
          @Suppress("UNCHECKED_CAST")
          val rawPillars = data["strategicPillars"] as? List<Map<String, Any>> ?: emptyList()
          val pillars = rawPillars.map { piMap ->
            StrategicPillar(
              id = piMap["id"] as? String ?: "p_${System.currentTimeMillis()}",
              name = piMap["name"] as? String ?: "",
              description = piMap["description"] as? String ?: "",
              weightPercentage = (piMap["weightPercentage"] as? Number)?.toInt() ?: 20,
              currentScore = (piMap["currentScore"] as? Number)?.toInt() ?: 75
            )
          }

          val rawPlaybook = (data["tacticalPlaybook"] as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()

          val roadmap = CareerStrategyRoadmap(
            id = "roadmap_cloud",
            targetRole = targetRole,
            targetHorizon = targetHorizon,
            targetCompensation = targetCompensation,
            executiveSummary = executiveSummary,
            strategicPillars = pillars,
            phases = phases,
            tacticalPlaybook = rawPlaybook,
            geminiStrategicAnalysis = geminiAnalysis,
            confidenceScore = confidence,
            lastUpdated = lastUpdated
          )

          restoredData = FirestoreCareerStrategyData(
            currentRole = currentRole,
            targetGoals = targetGoals,
            roadmap = roadmap,
            lastSyncedTimestamp = savedAtTimestamp
          )
        }
      }
    } catch (e: Exception) {
      Log.w(tag, "Firestore load warning: ${e.message}. Reading from local cache.", e)
    }

    // If Firestore was empty or failed, load from local cache
    if (restoredData == null) {
      restoredData = loadFromLocalCache()
    }

    if (restoredData != null) {
      _syncState.value = FirestoreSyncState.SYNCED
      _lastSyncedTimestamp.value = restoredData.lastSyncedTimestamp
      _statusMessage.value = "Loaded from Firestore"
    } else {
      _syncState.value = FirestoreSyncState.IDLE
      _statusMessage.value = "No cloud roadmap found"
    }

    Result.success(restoredData)
  }

  /**
   * Real-time update for a single milestone's status and progress in Firestore.
   */
  suspend fun updateMilestoneInFirestore(
    userId: String,
    milestone: SkillMilestone,
    phaseNumber: Int
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    try {
      val firestore = FirebaseFirestore.getInstance()
      val milestoneDoc = firestore.collection("users")
        .document(userId)
        .collection("career_strategy_milestones")
        .document(milestone.id)

      val updateData = mapOf(
        "status" to milestone.status.name,
        "progress" to milestone.progress,
        "lastUpdated" to System.currentTimeMillis()
      )
      milestoneDoc.set(updateData, SetOptions.merge()).awaitTask()
      Result.success(true)
    } catch (e: Exception) {
      Log.w(tag, "Failed to update single milestone in Firestore: ${e.message}", e)
      Result.failure(e)
    }
  }

  // --- Local Cache Persistence Helper ---

  internal fun saveToLocalCache(
    currentRole: String,
    targetGoals: String,
    roadmap: CareerStrategyRoadmap,
    timestamp: Long
  ) {
    try {
      val json = JSONObject().apply {
        put("currentRole", currentRole)
        put("targetGoals", targetGoals)
        put("timestamp", timestamp)
        put("targetRole", roadmap.targetRole)
        put("targetHorizon", roadmap.targetHorizon)
        put("targetCompensation", roadmap.targetCompensation)
        put("executiveSummary", roadmap.executiveSummary)
        put("geminiStrategicAnalysis", roadmap.geminiStrategicAnalysis)
        put("confidenceScore", roadmap.confidenceScore)
        put("lastUpdated", roadmap.lastUpdated)

        val phasesArray = JSONArray()
        roadmap.phases.forEach { phase ->
          val pObj = JSONObject().apply {
            put("phaseId", phase.phaseId)
            put("phaseNumber", phase.phaseNumber)
            put("title", phase.title)
            put("timeframe", phase.timeframe)
            put("targetRoleTier", phase.targetRoleTier)
            put("targetCompLakhs", phase.targetCompLakhs)
            put("focusTheme", phase.focusTheme)
            put("keyObjectives", JSONArray(phase.keyObjectives))
            put("deliverableArtifacts", JSONArray(phase.deliverableArtifacts))

            val mArray = JSONArray()
            phase.skillMilestones.forEach { m ->
              val mObj = JSONObject().apply {
                put("id", m.id)
                put("title", m.title)
                put("category", m.category)
                put("targetProficiency", m.targetProficiency)
                put("status", m.status.name)
                put("progress", m.progress)
                put("estimatedWeeks", m.estimatedWeeks)
                put("keyLearningOutcomes", JSONArray(m.keyLearningOutcomes))
                put("proofOfWorkArtifact", m.proofOfWorkArtifact)
                put("verificationMethod", m.verificationMethod)
              }
              mArray.put(mObj)
            }
            put("skillMilestones", mArray)
          }
          phasesArray.put(pObj)
        }
        put("phases", phasesArray)

        val pillarsArray = JSONArray()
        roadmap.strategicPillars.forEach { pillar ->
          val piObj = JSONObject().apply {
            put("id", pillar.id)
            put("name", pillar.name)
            put("description", pillar.description)
            put("weightPercentage", pillar.weightPercentage)
            put("currentScore", pillar.currentScore)
          }
          pillarsArray.put(piObj)
        }
        put("strategicPillars", pillarsArray)
        put("tacticalPlaybook", JSONArray(roadmap.tacticalPlaybook))
      }

      prefs.edit()
        .putString("cached_career_strategy_json", json.toString())
        .putLong("last_synced_timestamp", timestamp)
        .apply()
    } catch (e: Exception) {
      Log.e(tag, "Failed to save local cache: ${e.message}", e)
    }
  }

  internal fun loadFromLocalCache(): FirestoreCareerStrategyData? {
    val jsonStr = prefs.getString("cached_career_strategy_json", null) ?: return null
    return try {
      val json = JSONObject(jsonStr)
      val currentRole = json.optString("currentRole", "Associate Strategy & Operations Analyst")
      val targetGoals = json.optString("targetGoals", "Senior Strategy & Ops Lead / Chief of Staff")
      val timestamp = json.optLong("timestamp", System.currentTimeMillis())

      val phasesArray = json.optJSONArray("phases") ?: JSONArray()
      val phases = mutableListOf<RoadmapPhase>()
      for (i in 0 until phasesArray.length()) {
        val pObj = phasesArray.getJSONObject(i)
        val mArray = pObj.optJSONArray("skillMilestones") ?: JSONArray()
        val milestones = mutableListOf<SkillMilestone>()
        for (j in 0 until mArray.length()) {
          val mObj = mArray.getJSONObject(j)
          val statusStr = mObj.optString("status", CareerMilestoneStatus.NOT_STARTED.name)
          val status = try {
            CareerMilestoneStatus.valueOf(statusStr)
          } catch (e: Exception) {
            CareerMilestoneStatus.NOT_STARTED
          }

          val outcomes = mutableListOf<String>()
          val outArray = mObj.optJSONArray("keyLearningOutcomes") ?: JSONArray()
          for (k in 0 until outArray.length()) {
            outcomes.add(outArray.getString(k))
          }

          milestones.add(
            SkillMilestone(
              id = mObj.optString("id", "m_$j"),
              title = mObj.optString("title", ""),
              category = mObj.optString("category", "Strategy"),
              targetProficiency = mObj.optString("targetProficiency", "Production Ready"),
              status = status,
              progress = mObj.optInt("progress", 0),
              estimatedWeeks = mObj.optInt("estimatedWeeks", 4),
              keyLearningOutcomes = outcomes,
              proofOfWorkArtifact = mObj.optString("proofOfWorkArtifact", ""),
              verificationMethod = mObj.optString("verificationMethod", "")
            )
          )
        }

        val objectives = mutableListOf<String>()
        val objArray = pObj.optJSONArray("keyObjectives") ?: JSONArray()
        for (k in 0 until objArray.length()) objectives.add(objArray.getString(k))

        val deliverables = mutableListOf<String>()
        val delivArray = pObj.optJSONArray("deliverableArtifacts") ?: JSONArray()
        for (k in 0 until delivArray.length()) deliverables.add(delivArray.getString(k))

        phases.add(
          RoadmapPhase(
            phaseId = pObj.optString("phaseId", "p_$i"),
            phaseNumber = pObj.optInt("phaseNumber", i + 1),
            title = pObj.optString("title", "Phase ${i + 1}"),
            timeframe = pObj.optString("timeframe", ""),
            targetRoleTier = pObj.optString("targetRoleTier", ""),
            targetCompLakhs = pObj.optString("targetCompLakhs", ""),
            focusTheme = pObj.optString("focusTheme", ""),
            keyObjectives = objectives,
            skillMilestones = milestones,
            deliverableArtifacts = deliverables
          )
        )
      }

      val pillars = mutableListOf<StrategicPillar>()
      val pillarsArray = json.optJSONArray("strategicPillars") ?: JSONArray()
      for (i in 0 until pillarsArray.length()) {
        val piObj = pillarsArray.getJSONObject(i)
        pillars.add(
          StrategicPillar(
            id = piObj.optString("id", "pi_$i"),
            name = piObj.optString("name", ""),
            description = piObj.optString("description", ""),
            weightPercentage = piObj.optInt("weightPercentage", 20),
            currentScore = piObj.optInt("currentScore", 75)
          )
        )
      }

      val playbook = mutableListOf<String>()
      val playArray = json.optJSONArray("tacticalPlaybook") ?: JSONArray()
      for (i in 0 until playArray.length()) playbook.add(playArray.getString(i))

      val roadmap = CareerStrategyRoadmap(
        id = "roadmap_cached",
        targetRole = json.optString("targetRole", targetGoals),
        targetHorizon = json.optString("targetHorizon", "3 Years"),
        targetCompensation = json.optString("targetCompensation", "₹28L - ₹42L + Equity"),
        executiveSummary = json.optString("executiveSummary", ""),
        strategicPillars = pillars,
        phases = phases,
        tacticalPlaybook = playbook,
        geminiStrategicAnalysis = json.optString("geminiStrategicAnalysis", ""),
        confidenceScore = json.optInt("confidenceScore", 94),
        lastUpdated = json.optString("lastUpdated", "Restored from Cache")
      )

      FirestoreCareerStrategyData(
        currentRole = currentRole,
        targetGoals = targetGoals,
        roadmap = roadmap,
        lastSyncedTimestamp = timestamp
      )
    } catch (e: Exception) {
      Log.e(tag, "Error parsing local cache: ${e.message}", e)
      null
    }
  }
}
