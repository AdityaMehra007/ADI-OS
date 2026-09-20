package com.example.data.model

/**
 * Data models for LinkedIn API Integration, Firebase Auth OAuth linking,
 * and automatic ingestion of professional history, skills, and network activity.
 */

enum class LinkedInConnectionStatus {
  DISCONNECTED,
  CONNECTING,
  CONNECTED,
  SYNCING,
  SYNC_SUCCESS,
  ERROR
}

data class LinkedInProfile(
  val memberId: String,
  val firstName: String,
  val lastName: String,
  val fullName: String,
  val headline: String,
  val vanityName: String = "adi-executive",
  val profilePictureUrl: String? = null,
  val email: String? = null,
  val location: String = "Bengaluru, Karnataka, India",
  val industry: String = "Technology & Operations",
  val summary: String = "",
  val connectionsCount: Int = 840,
  val publicProfileUrl: String = "https://linkedin.com/in/adi-executive",
  val isVerified: Boolean = true,
  val lastSyncedAt: Long = System.currentTimeMillis()
)

data class LinkedInExperience(
  val id: String,
  val title: String,
  val companyName: String,
  val companyLogoUrl: String? = null,
  val employmentType: String = "Full-time",
  val location: String = "Bengaluru, India",
  val startDate: String,
  val endDate: String? = null,
  val isCurrent: Boolean = false,
  val description: String,
  val skillsAcquired: List<String> = emptyList(),
  val keyImpactMetrics: List<String> = emptyList()
)

data class LinkedInEducation(
  val id: String,
  val schoolName: String,
  val degree: String,
  val fieldOfStudy: String,
  val startDate: String,
  val endDate: String,
  val gradeOrHonors: String? = null,
  val activitiesAndSocieties: String? = null
)

data class LinkedInSkill(
  val id: String,
  val name: String,
  val endorsementCount: Int = 0,
  val category: String = "OPERATIONS",
  val isVerified: Boolean = false
)

data class LinkedInNetworkActivityItem(
  val id: String,
  val activityType: String, // RECRUITER_INMAIL, CONNECTION_REQUEST, POST_ENGAGEMENT, SKILL_ENDORSEMENT, HIRING_ALERT
  val actorName: String,
  val actorTitle: String,
  val actorCompany: String,
  val actorProfileUrl: String = "https://linkedin.com",
  val actorAvatarUrl: String? = null,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val timeFormatted: String,
  val isActionable: Boolean = true,
  val recommendedAction: String? = null,
  val status: String = "NEW" // NEW, LOGGED_TO_CRM, ACTIONED
)

data class LinkedInNetworkContact(
  val id: String,
  val name: String,
  val headline: String,
  val company: String,
  val companyId: String,
  val location: String = "Bengaluru, India",
  val avatarUrl: String? = null,
  val relationshipDegree: String = "1st", // 1st, 2nd, Recruiter, Alumni
  val connectedDate: String,
  val linkedinUrl: String,
  val email: String? = null,
  val mutualConnectionsCount: Int = 18,
  val isImportedToCrm: Boolean = false
)

data class LinkedInSyncTelemetry(
  val syncId: String = "sync_${System.currentTimeMillis()}",
  val timestamp: Long = System.currentTimeMillis(),
  val timeFormatted: String,
  val status: String = "SUCCESS",
  val experiencesCount: Int = 0,
  val educationCount: Int = 0,
  val skillsCount: Int = 0,
  val contactsImportedCount: Int = 0,
  val networkActivitiesCount: Int = 0,
  val headlineImported: String = "",
  val isLiveOAuth: Boolean = true,
  val importedFactsSummary: List<String> = emptyList(),
  val executionDurationMs: Long = 0L
)
