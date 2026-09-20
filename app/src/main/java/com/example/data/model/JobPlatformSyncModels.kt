package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Enumeration of external job and recruitment platforms supported by the background sync service.
 */
enum class JobPlatform(
  val platformKey: String,
  val displayName: String,
  val logoEmoji: String,
  val brandColorHex: String,
  val defaultPortalUrl: String,
  val atsType: String // e.g., "ENTERPRISE_ATS", "TECH_PORTAL", "STARTUP_NETWORK"
) {
  LINKEDIN(
    platformKey = "LINKEDIN",
    displayName = "LinkedIn",
    logoEmoji = "💼",
    brandColorHex = "#0A66C2",
    defaultPortalUrl = "https://www.linkedin.com/jobs",
    atsType = "PROFESSIONAL_NETWORK"
  ),
  GREENHOUSE(
    platformKey = "GREENHOUSE",
    displayName = "Greenhouse ATS",
    logoEmoji = "🌱",
    brandColorHex = "#24A148",
    defaultPortalUrl = "https://boards.greenhouse.io",
    atsType = "ENTERPRISE_ATS"
  ),
  LEVER(
    platformKey = "LEVER",
    displayName = "Lever ATS",
    logoEmoji = "⚡",
    brandColorHex = "#5552D9",
    defaultPortalUrl = "https://jobs.lever.co",
    atsType = "MODERN_ATS"
  ),
  WORKDAY(
    platformKey = "WORKDAY",
    displayName = "Workday Portal",
    logoEmoji = "🏢",
    brandColorHex = "#E28743",
    defaultPortalUrl = "https://myworkdayjobs.com",
    atsType = "ENTERPRISE_ERP"
  ),
  INDEED(
    platformKey = "INDEED",
    displayName = "Indeed",
    logoEmoji = "🔍",
    brandColorHex = "#2164F3",
    defaultPortalUrl = "https://www.indeed.com",
    atsType = "AGGREGATOR"
  ),
  WELLFOUND(
    platformKey = "WELLFOUND",
    displayName = "Wellfound",
    logoEmoji = "✌️",
    brandColorHex = "#FF5A5F",
    defaultPortalUrl = "https://wellfound.com",
    atsType = "STARTUP_ATS"
  ),
  DIRECT_CAREERS(
    platformKey = "DIRECT_CAREERS",
    displayName = "Company Portal",
    logoEmoji = "🌐",
    brandColorHex = "#00ADB5",
    defaultPortalUrl = "https://careers.google.com",
    atsType = "DIRECT_CAREERS"
  );

  fun getBrandColor(): Color {
    return try {
      Color(android.graphics.Color.parseColor(brandColorHex))
    } catch (e: Exception) {
      Color(0xFF00ADB5)
    }
  }

  companion object {
    fun fromKey(key: String?): JobPlatform {
      if (key.isNullOrBlank()) return DIRECT_CAREERS
      return entries.find { it.platformKey.equals(key, ignoreCase = true) || it.displayName.equals(key, ignoreCase = true) }
        ?: when {
          key.contains("linkedin", ignoreCase = true) -> LINKEDIN
          key.contains("greenhouse", ignoreCase = true) -> GREENHOUSE
          key.contains("lever", ignoreCase = true) -> LEVER
          key.contains("workday", ignoreCase = true) -> WORKDAY
          key.contains("indeed", ignoreCase = true) -> INDEED
          key.contains("wellfound", ignoreCase = true) || key.contains("angellist", ignoreCase = true) -> WELLFOUND
          else -> DIRECT_CAREERS
        }
    }
  }
}

/**
 * Represents a saved job listing pulled and synchronized from an external platform.
 */
data class PlatformSavedJobListing(
  val id: String,
  val externalListingId: String,
  val platform: JobPlatform,
  val companyName: String,
  val title: String,
  val location: String,
  val salaryRange: String,
  val roleFamily: String, // STRATEGY_OPS, BUSINESS_ANALYST, AI_OPERATIONS, CONSULTING
  val applicationUrl: String,
  val savedTimestamp: Long = System.currentTimeMillis(),
  val savedDateFormatted: String,
  val fitScore: Int,
  val isFresherFriendly: Boolean = true,
  val syncStatus: String = "SYNCED", // SYNCED, UPDATED, NEW_DISCOVERY
  val whyItFits: String,
  val requiredExperience: String = "0-2 Years",
  val deadlineFormatted: String? = null
)

/**
 * Tracks an application's recruitment lifecycle across platforms (e.g. Greenhouse, Workday, Lever, LinkedIn).
 */
data class PlatformApplicationStatus(
  val applicationId: String,
  val companyName: String,
  val roleTitle: String,
  val platform: JobPlatform,
  val platformJobId: String = "",
  val currentStatus: String, // APPLIED, VIEWED, ASSESSMENT, INTERVIEW, FINAL_ROUND, OFFER, REJECTED, WITHDRAWN
  val previousStatus: String? = null,
  val lastCheckedTimestamp: Long = System.currentTimeMillis(),
  val formattedCheckTime: String,
  val hasTransitioned: Boolean = false,
  val platformStageName: String, // e.g. "Hiring Manager Review", "Take-Home Case Study", "Executive Partner Round"
  val transitionNotes: String = "",
  val actionRequired: String? = null, // e.g. "Submit case by Tuesday", "Schedule screening slot"
  val stageProgressPercent: Int = 30 // 0-100% through the hiring pipeline
)

/**
 * Telemetry summary generated after each periodic background synchronization cycle.
 */
data class PlatformSyncResult(
  val syncId: String = java.util.UUID.randomUUID().toString(),
  val timestamp: Long = System.currentTimeMillis(),
  val formattedTime: String,
  val durationMs: Long,
  val source: String, // BACKGROUND_ALARM, MANUAL_UI, SYSTEM_BOOT
  val isSuccess: Boolean,
  val platformsPolled: List<JobPlatform>,
  val savedJobsPolledCount: Int,
  val newSavedJobsAdded: Int,
  val applicationStatusesCheckedCount: Int,
  val statusTransitionsCount: Int,
  val statusTransitions: List<PlatformApplicationStatus> = emptyList(),
  val summaryMessage: String
)

/**
 * Health and operational status of a specific platform connector.
 */
data class PlatformHealthStatus(
  val platform: JobPlatform,
  val isConnected: Boolean = true,
  val lastSyncedFormatted: String = "Just now",
  val savedJobsCount: Int = 0,
  val monitoredApplicationsCount: Int = 0,
  val activeAlertsCount: Int = 0,
  val latencyMs: Long = 180L
)

/**
 * Configuration preferences for the background sync daemon.
 */
data class PlatformSyncDaemonConfig(
  val isPeriodicEnabled: Boolean = true,
  val intervalMinutes: Int = 30, // 15, 30, 60, 120, 360, 720
  val notifyOnSavedJobSync: Boolean = true,
  val notifyOnStatusChange: Boolean = true,
  val autoAdvanceLocalStatus: Boolean = true,
  val enabledPlatforms: Set<JobPlatform> = JobPlatform.entries.toSet()
)
