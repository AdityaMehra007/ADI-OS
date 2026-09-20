package com.example.data.model

/**
 * Predefined Quick Action automation types available from the Quick Action Floating Button.
 */
enum class QuickActionType(
  val displayName: String,
  val defaultDescription: String,
  val badgeText: String,
  val taskId: String
) {
  SYNC_COMPANY_INTEL(
    displayName = "Sync Company Intel",
    defaultDescription = "Fetch latest Firestore company dossier & executive news",
    badgeText = "FIRESTORE",
    taskId = "TASK_SYNC_COMPANY_INTEL"
  ),
  SUMMARIZE_CAREER_NOTE(
    displayName = "Summarize Career Note",
    defaultDescription = "Synthesize strategic memo into executive battle-plan using AI",
    badgeText = "GEMINI AI",
    taskId = "TASK_SUMMARIZE_CAREER_NOTE"
  ),
  RUN_JOB_RADAR(
    displayName = "Run Job Discovery Radar",
    defaultDescription = "Scan target tech hubs for high-match opportunities",
    badgeText = "CRAWLER",
    taskId = "TASK_JOB_RADAR_CRAWLER"
  ),
  AUDIT_CAREER_HEALTH(
    displayName = "Audit Career Health",
    defaultDescription = "Evaluate application velocity & interview readiness score",
    badgeText = "TELEMETRY",
    taskId = "TASK_CAREER_HEALTH_AUDIT"
  ),
  TRIGGER_AUTOMATION_RULE(
    displayName = "Automation Rule Engine",
    defaultDescription = "Execute event-triggered autonomous rules & responses",
    badgeText = "RULES",
    taskId = "TASK_AUTOMATION_RULE_ENGINE"
  )
}

/**
 * Metadata for a predefined Quick Action item displayed in the Speed Dial / Tray.
 */
data class PredefinedQuickAction(
  val type: QuickActionType,
  val title: String = type.displayName,
  val description: String = type.defaultDescription,
  val badge: String = type.badgeText,
  val isHighPriority: Boolean = true
)

/**
 * Result of an AI-driven Career Note Summarization.
 */
data class CareerNoteSummaryResult(
  val noteId: String,
  val noteTitle: String,
  val category: String = "STRATEGY",
  val targetCompany: String = "",
  val targetRole: String = "",
  val originalContentSnippet: String = "",
  val executiveBrief: String,
  val keyTakeaways: List<String>,
  val strategicActionItems: List<String>,
  val interviewTalkingPoints: List<String>,
  val confidenceScore: Int = 96,
  val generatedAt: Long = System.currentTimeMillis()
)

/**
 * Real-time telemetry feedback of a triggered Quick Action.
 */
data class QuickActionExecutionTelemetry(
  val actionType: QuickActionType,
  val taskName: String,
  val status: String = "SUCCESS", // "RUNNING", "SUCCESS", "FAILED"
  val message: String,
  val durationMs: Long = 0L,
  val timestamp: Long = System.currentTimeMillis()
)
