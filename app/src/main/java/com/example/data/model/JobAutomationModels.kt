package com.example.data.model

data class AutomationCriteria(
  val targetRoles: List<String> = listOf("Business Analyst", "Strategy Analyst", "Operations Associate", "Chief of Staff"),
  val targetLocations: List<String> = listOf("Bengaluru", "Hybrid", "Remote"),
  val minFitScore: Int = 85,
  val minSalaryLakhs: Int = 12,
  val onlyFresherFriendly: Boolean = true,
  val autoGenerateTailoredResume: Boolean = true,
  val autoDraftCoverLetter: Boolean = true,
  val requireHumanApprovalBeforeSubmit: Boolean = true,
  val targetTiers: List<String> = listOf("S+", "S", "A")
)

data class AutomatedJobMatch(
  val job: Job,
  val matchScore: Int,
  val keyMatchFactors: List<String>,
  val missingGaps: List<String>,
  val recommendedPitch: String,
  val automationStatus: AutomationQueueStatus = AutomationQueueStatus.QUEUED,
  val tailoredResumeGenerated: Boolean = true,
  val coverLetterGenerated: Boolean = true
)

enum class AutomationQueueStatus {
  QUEUED,
  READY_FOR_APPROVAL,
  SUBMITTED,
  SKIPPED
}
