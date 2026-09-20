package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class HabitCategory(val displayName: String, val defaultIconKey: String) {
  NETWORKING("Networking Outreach", "PEOPLE"),
  SKILLS("Skill Building", "CODE"),
  APPLICATIONS("Job Pipeline", "SEND"),
  RESEARCH("Market Intel", "LIGHTBULB"),
  CUSTOM("Custom Ritual", "BOLT")
}

data class DailyHabitItem(
  val id: String,
  val title: String,
  val description: String,
  val category: String, // NETWORKING, SKILLS, APPLICATIONS, RESEARCH, CUSTOM
  val targetCount: Int = 1,
  val currentCount: Int = 0,
  val isCompleted: Boolean = false,
  val dateKey: String = getTodayDateKey(),
  val iconKey: String = "BOLT",
  val orderIndex: Int = 0,
  val lastUpdatedTimestamp: Long = System.currentTimeMillis()
) {
  val isFullyCompleted: Boolean get() = isCompleted || currentCount >= targetCount
  val progressFraction: Float get() = if (targetCount <= 0) 1f else (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)
}

data class CareerMomentumData(
  val dateKey: String = getTodayDateKey(),
  val habits: List<DailyHabitItem> = emptyList(),
  val streakDays: Int = 5,
  val lastSyncedTimestamp: Long = 0L,
  val isSyncedToFirestore: Boolean = false,
  val firestoreDocumentPath: String = "users/current/career_momentum/today"
) {
  val completedHabitsCount: Int get() = habits.count { it.isFullyCompleted }
  val totalHabitsCount: Int get() = habits.size
  val progressFraction: Float get() = if (habits.isEmpty()) 0f else (completedHabitsCount.toFloat() / habits.size.toFloat()).coerceIn(0f, 1f)
  val progressPercentage: Int get() = (progressFraction * 100).toInt()

  val momentumTier: String get() = when {
    progressPercentage >= 100 -> "SUPERCHARGED"
    progressPercentage >= 75 -> "HIGH MOMENTUM"
    progressPercentage >= 50 -> "SOLID VELOCITY"
    progressPercentage > 0 -> "IGNITION"
    else -> "READY TO LAUNCH"
  }

  val momentumSummary: String get() = when {
    progressPercentage >= 100 -> "All high-impact career levers pulled today! Unstoppable execution."
    progressPercentage >= 75 -> "Strong forward thrust. Closing in on peak daily output."
    progressPercentage >= 50 -> "Halfway to daily target. Keep the daily cadence firing."
    progressPercentage > 0 -> "Initial momentum activated. Complete your remaining daily habits."
    else -> "Prime your day with strategic networking outreach and deliberate practice."
  }
}

fun getTodayDateKey(): String {
  val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
  return sdf.format(Date())
}
