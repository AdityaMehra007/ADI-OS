package com.example.notification

enum class NotificationType {
  HIGH_MATCH_JOB,
  COMPANY_INTEL,
  APPLICATION_STATUS,
  SYSTEM_ALERT
}

enum class NotificationPriority {
  NORMAL,
  HIGH,
  CRITICAL
}

data class NotificationAlertItem(
  val id: String = "notif_${System.currentTimeMillis()}_${(100..999).random()}",
  val type: NotificationType,
  val title: String,
  val message: String,
  val detailedSummary: String = "",
  val timestamp: String = "Just now",
  val timestampMillis: Long = System.currentTimeMillis(),
  val targetScreen: String, // "JOBS", "COMPANIES", "APPLICATIONS"
  val targetItemId: String = "",
  val targetItemName: String = "",
  val matchScore: Int? = null,
  val impactScore: Int? = null,
  val priority: NotificationPriority = NotificationPriority.HIGH,
  val isRead: Boolean = false
)

data class NotificationPreferences(
  val notificationsEnabled: Boolean = true,
  val minFitScoreThreshold: Int = 85,
  val notifyFollowedCompaniesOnly: Boolean = true,
  val notifyOnCriticalNews: Boolean = true,
  val notifyOnApplicationStatus: Boolean = true,
  val soundAndVibration: Boolean = true,
  val headsUpAlerts: Boolean = true
)
