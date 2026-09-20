package com.example.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.CareerMomentumData
import com.example.data.model.DailyHabitItem
import com.example.data.model.getTodayDateKey
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Service to sync and visualize Career Momentum habits and daily progress
 * in Firebase Firestore under users/{userId}/career_momentum/today and daily history.
 */
class FirestoreCareerMomentumService(private val context: Context) {
  private val tag = "FirestoreCareerMomentum"
  private val prefs: SharedPreferences =
    context.getSharedPreferences("titan_career_momentum_vault", Context.MODE_PRIVATE)

  private val _syncState = MutableStateFlow(FirestoreSyncState.IDLE)
  val syncState: StateFlow<FirestoreSyncState> = _syncState.asStateFlow()

  private val _lastSyncedTimestamp = MutableStateFlow(
    prefs.getLong("last_synced_timestamp", 0L).takeIf { it > 0 }
  )
  val lastSyncedTimestamp: StateFlow<Long?> = _lastSyncedTimestamp.asStateFlow()

  private val _statusMessage = MutableStateFlow(
    if (prefs.getLong("last_synced_timestamp", 0L) > 0) "Synchronized with Firestore" else "Firestore Ready"
  )
  val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

  /**
   * Saves daily habits, target counts, and calculated progress bar percentage to Firebase Firestore.
   */
  suspend fun saveMomentumToFirestore(
    userId: String,
    momentum: CareerMomentumData
  ): Result<Boolean> = withContext(Dispatchers.IO) {
    _syncState.value = FirestoreSyncState.SYNCING
    _statusMessage.value = "Saving ${momentum.progressPercentage}% progress to Firestore..."

    var firestoreSuccess = false
    val now = System.currentTimeMillis()

    try {
      val firestore = FirebaseFirestore.getInstance()
      val todayDocRef = firestore.collection("users")
        .document(userId)
        .collection("career_momentum")
        .document("today")

      val dateHistoryDocRef = firestore.collection("users")
        .document(userId)
        .collection("career_momentum_history")
        .document(momentum.dateKey)

      val habitsPayload = momentum.habits.map { habit ->
        hashMapOf(
          "id" to habit.id,
          "title" to habit.title,
          "description" to habit.description,
          "category" to habit.category,
          "targetCount" to habit.targetCount,
          "currentCount" to habit.currentCount,
          "isCompleted" to habit.isFullyCompleted,
          "iconKey" to habit.iconKey,
          "orderIndex" to habit.orderIndex,
          "lastUpdatedTimestamp" to habit.lastUpdatedTimestamp
        )
      }

      val data = hashMapOf(
        "dateKey" to momentum.dateKey,
        "streakDays" to momentum.streakDays,
        "progressPercentage" to momentum.progressPercentage,
        "progressFraction" to momentum.progressFraction.toDouble(),
        "completedHabitsCount" to momentum.completedHabitsCount,
        "totalHabitsCount" to momentum.totalHabitsCount,
        "momentumTier" to momentum.momentumTier,
        "habits" to habitsPayload,
        "lastSyncedTimestamp" to now,
        "userId" to userId
      )

      todayDocRef.set(data, SetOptions.merge()).await()
      dateHistoryDocRef.set(data, SetOptions.merge()).await()
      firestoreSuccess = true
      Log.i(tag, "Successfully synced ${momentum.progressPercentage}% progress bar to Firestore")
    } catch (e: Exception) {
      Log.w(tag, "Firestore remote write failed or offline: ${e.message}. Using local storage fallback.")
    }

    // Save locally for instant persistence
    saveToLocalCache(momentum.copy(lastSyncedTimestamp = now, isSyncedToFirestore = firestoreSuccess))

    _lastSyncedTimestamp.value = now
    prefs.edit().putLong("last_synced_timestamp", now).apply()

    _syncState.value = if (firestoreSuccess) FirestoreSyncState.SYNCED else FirestoreSyncState.FAILED
    _statusMessage.value = if (firestoreSuccess) {
      "Synced to Firestore (${momentum.progressPercentage}%)"
    } else {
      "Saved locally (${momentum.progressPercentage}%) — offline"
    }

    Result.success(firestoreSuccess)
  }

  /**
   * Retrieves today's momentum from Firebase Firestore with local cache fallback.
   */
  suspend fun loadMomentumFromFirestore(
    userId: String,
    dateKey: String = getTodayDateKey()
  ): Result<CareerMomentumData?> = withContext(Dispatchers.IO) {
    try {
      val firestore = FirebaseFirestore.getInstance()
      val doc = firestore.collection("users")
        .document(userId)
        .collection("career_momentum")
        .document("today")
        .get()
        .await()

      if (doc.exists()) {
        val serverDateKey = doc.getString("dateKey") ?: dateKey
        val streak = (doc.getLong("streakDays") ?: 5L).toInt()
        val lastSynced = doc.getLong("lastSyncedTimestamp") ?: System.currentTimeMillis()

        @Suppress("UNCHECKED_CAST")
        val rawHabits = doc.get("habits") as? List<Map<String, Any>>
        val habits = rawHabits?.mapIndexed { index, map ->
          DailyHabitItem(
            id = map["id"] as? String ?: "habit_$index",
            title = map["title"] as? String ?: "Habit",
            description = map["description"] as? String ?: "",
            category = map["category"] as? String ?: "NETWORKING",
            targetCount = (map["targetCount"] as? Number)?.toInt() ?: 1,
            currentCount = (map["currentCount"] as? Number)?.toInt() ?: 0,
            isCompleted = map["isCompleted"] as? Boolean ?: false,
            dateKey = serverDateKey,
            iconKey = map["iconKey"] as? String ?: "BOLT",
            orderIndex = (map["orderIndex"] as? Number)?.toInt() ?: index,
            lastUpdatedTimestamp = (map["lastUpdatedTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
          )
        } ?: emptyList()

        val momentum = CareerMomentumData(
          dateKey = serverDateKey,
          habits = habits,
          streakDays = streak,
          lastSyncedTimestamp = lastSynced,
          isSyncedToFirestore = true,
          firestoreDocumentPath = "users/$userId/career_momentum/today"
        )
        saveToLocalCache(momentum)
        _syncState.value = FirestoreSyncState.SYNCED
        _statusMessage.value = "Loaded from Firestore (${momentum.progressPercentage}%)"
        return@withContext Result.success(momentum)
      }
    } catch (e: Exception) {
      Log.w(tag, "Failed to fetch from Firestore: ${e.message}. Falling back to local cache.")
    }

    val cached = loadFromLocalCache(dateKey)
    Result.success(cached)
  }

  private fun saveToLocalCache(momentum: CareerMomentumData) {
    try {
      val root = JSONObject()
      root.put("dateKey", momentum.dateKey)
      root.put("streakDays", momentum.streakDays)
      root.put("lastSyncedTimestamp", momentum.lastSyncedTimestamp)
      root.put("isSyncedToFirestore", momentum.isSyncedToFirestore)

      val habitsArray = JSONArray()
      momentum.habits.forEach { habit ->
        val h = JSONObject()
        h.put("id", habit.id)
        h.put("title", habit.title)
        h.put("description", habit.description)
        h.put("category", habit.category)
        h.put("targetCount", habit.targetCount)
        h.put("currentCount", habit.currentCount)
        h.put("isCompleted", habit.isCompleted)
        h.put("dateKey", habit.dateKey)
        h.put("iconKey", habit.iconKey)
        h.put("orderIndex", habit.orderIndex)
        h.put("lastUpdatedTimestamp", habit.lastUpdatedTimestamp)
        habitsArray.put(h)
      }
      root.put("habits", habitsArray)

      prefs.edit().putString("cached_momentum_${momentum.dateKey}", root.toString()).apply()
      prefs.edit().putString("cached_momentum_latest", root.toString()).apply()
    } catch (e: Exception) {
      Log.e(tag, "Failed to cache momentum locally: ${e.message}")
    }
  }

  fun loadFromLocalCache(dateKey: String = getTodayDateKey()): CareerMomentumData? {
    val rawJson = prefs.getString("cached_momentum_$dateKey", null)
      ?: prefs.getString("cached_momentum_latest", null)
      ?: return null

    return try {
      val root = JSONObject(rawJson)
      val dKey = root.optString("dateKey", dateKey)
      val streak = root.optInt("streakDays", 5)
      val syncedTime = root.optLong("lastSyncedTimestamp", 0L)
      val isSynced = root.optBoolean("isSyncedToFirestore", false)

      val habitsArray = root.optJSONArray("habits")
      val habits = mutableListOf<DailyHabitItem>()
      if (habitsArray != null) {
        for (i in 0 until habitsArray.length()) {
          val h = habitsArray.getJSONObject(i)
          habits.add(
            DailyHabitItem(
              id = h.optString("id", "habit_$i"),
              title = h.optString("title", ""),
              description = h.optString("description", ""),
              category = h.optString("category", "NETWORKING"),
              targetCount = h.optInt("targetCount", 1),
              currentCount = h.optInt("currentCount", 0),
              isCompleted = h.optBoolean("isCompleted", false),
              dateKey = h.optString("dateKey", dKey),
              iconKey = h.optString("iconKey", "BOLT"),
              orderIndex = h.optInt("orderIndex", i),
              lastUpdatedTimestamp = h.optLong("lastUpdatedTimestamp", System.currentTimeMillis())
            )
          )
        }
      }

      CareerMomentumData(
        dateKey = dKey,
        habits = habits,
        streakDays = streak,
        lastSyncedTimestamp = syncedTime,
        isSyncedToFirestore = isSynced
      )
    } catch (e: Exception) {
      Log.e(tag, "Error parsing cached momentum: ${e.message}")
      null
    }
  }

  fun generateDefaultHabits(dateKey: String = getTodayDateKey()): List<DailyHabitItem> {
    return listOf(
      DailyHabitItem(
        id = "habit_networking",
        title = "Networking outreach",
        description = "Reach out to 2 hiring managers, founders, or alumni with high-conviction notes",
        category = "NETWORKING",
        targetCount = 2,
        currentCount = 1,
        isCompleted = false,
        dateKey = dateKey,
        iconKey = "PEOPLE",
        orderIndex = 0
      ),
      DailyHabitItem(
        id = "habit_skills",
        title = "Skill building",
        description = "Complete 30m of core competence drill (System Design, Unit Economics, or AI prompts)",
        category = "SKILLS",
        targetCount = 1,
        currentCount = 1,
        isCompleted = true,
        dateKey = dateKey,
        iconKey = "CODE",
        orderIndex = 1
      ),
      DailyHabitItem(
        id = "habit_pipeline",
        title = "Job pipeline submission",
        description = "Submit 1 high-alignment application or follow up with an active interview recruiter",
        category = "APPLICATIONS",
        targetCount = 1,
        currentCount = 0,
        isCompleted = false,
        dateKey = dateKey,
        iconKey = "SEND",
        orderIndex = 2
      ),
      DailyHabitItem(
        id = "habit_intel",
        title = "Market intel & case study",
        description = "Read 1 sector breakdown or review STAR stories for executive interview readiness",
        category = "RESEARCH",
        targetCount = 1,
        currentCount = 1,
        isCompleted = true,
        dateKey = dateKey,
        iconKey = "LIGHTBULB",
        orderIndex = 3
      )
    )
  }
}
