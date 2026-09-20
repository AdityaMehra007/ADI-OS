package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * CareerNote entity for Room database local data storage.
 * Stores local user career strategy notes, tactical memos, interview battle-plans,
 * and salary negotiation scripts in Room for offline accessibility.
 */
@Entity(tableName = "career_notes")
data class CareerNote(
  @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
  val title: String,
  val category: String = "STRATEGY", // STRATEGY, INTERVIEW_PREP, NEGOTIATION, NETWORKING, TARGET_ROLES, GENERAL
  val content: String,
  val targetCompany: String = "",
  val targetRole: String = "",
  val tags: String = "",
  val isPinned: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val isOfflineAvailable: Boolean = true
)

/**
 * Stores local user career strategy notes, tactical memos, interview battle-plans,
 * and salary negotiation scripts in Room for offline accessibility.
 */
@Entity(tableName = "career_strategy_notes")
data class CareerStrategyNote(
  @PrimaryKey val id: String,
  val title: String,
  val category: String = "STRATEGY", // STRATEGY, INTERVIEW_PREP, NEGOTIATION, NETWORKING, TARGET_ROLES, GENERAL
  val content: String,
  val targetCompany: String = "",
  val targetRole: String = "",
  val tags: String = "",
  val isPinned: Boolean = false,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val isOfflineAvailable: Boolean = true
)

/**
 * Stores bookmarked companies with custom priority tiers, personal candidate notes,
 * and radar alerts in Room for offline review and quick access.
 */
@Entity(tableName = "company_bookmarks")
data class CompanyBookmark(
  @PrimaryKey val companyId: String,
  val companyName: String,
  val logoEmoji: String = "🏢",
  val industry: String = "",
  val tier: String = "S", // S+, S, A, B, C
  val priority: String = "HIGH", // DREAM, HIGH, MEDIUM, WATCH
  val personalNotes: String = "",
  val targetRole: String = "",
  val bookmarkedAt: Long = System.currentTimeMillis(),
  val alertOnNewJobs: Boolean = true
)

/**
 * Stores comprehensive audit and operational logs of background automation tasks,
 * periodic job discovery pulls, and agent triggers in Room for complete offline transparency.
 */
@Entity(tableName = "automation_task_logs")
data class AutomationTaskLog(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val taskId: String, // e.g. "JOB_RADAR_SWARM", "APPLICATION_ASSISTANT", "MARKET_INTELLIGENCE"
  val taskName: String,
  val triggerSource: String, // e.g. "BACKGROUND_ALARM", "PERIODIC_RADAR", "MANUAL_SWARM"
  val timestamp: Long = System.currentTimeMillis(),
  val formattedTime: String,
  val status: String = "SUCCESS", // SUCCESS, FAILED, RUNNING, WARNING
  val summary: String,
  val details: String = "",
  val itemsDiscoveredCount: Int = 0,
  val highPriorityMatchesCount: Int = 0,
  val executionDurationMs: Long = 0L
)

/**
 * AutomationTask entity for Room database local data storage.
 * Models automated background career tasks, periodic job crawlers, agent workflows,
 * and execution policies in Room for local data storage and scheduling.
 */
@Entity(tableName = "automation_tasks")
data class AutomationTask(
  @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
  val title: String,
  val taskType: String = "JOB_RADAR", // JOB_RADAR, RESUME_REFRESH, APPLICATION_FOLLOW_UP, INTERVIEW_PREP, MARKET_INTEL, PROFILE_AUDIT
  val description: String = "",
  val frequency: String = "DAILY", // HOURLY, DAILY, WEEKLY, ON_DEMAND
  val triggerCondition: String = "SCHEDULED_PERIODIC", // SCHEDULED_PERIODIC, EVENT_TRIGGERED, MANUAL
  val isEnabled: Boolean = true,
  val lastExecutedTimestamp: Long? = null,
  val nextScheduledTimestamp: Long? = null,
  val status: String = "IDLE", // IDLE, RUNNING, COMPLETED, FAILED, PAUSED
  val priorityLevel: String = "HIGH", // CRITICAL, HIGH, MEDIUM, LOW
  val executionCount: Int = 0,
  val successCount: Int = 0,
  val failureCount: Int = 0,
  val lastResultSummary: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)

/**
 * AutomationRule entity for Room database local data storage.
 * Stores user-configured automation rules defining custom trigger conditions
 * (e.g. "If email from company X received", "If interview scheduled with Google")
 * and automated responses (e.g. auto-drafting email reply, calendar follow-up,
 * generating interview prep brief, updating CRM pipeline).
 */
@Entity(tableName = "automation_rules")
data class AutomationRule(
  @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
  val name: String,
  val triggerCategory: String = "EMAIL_RECEIVED", // EMAIL_RECEIVED, APPLICATION_STATUS, INTERVIEW_INVITE, JOB_RADAR, RECRUITER_OUTREACH, SCHEDULED_TIMER, CUSTOM_TRIGGER
  val targetCompany: String = "", // e.g. "Zepto", "Google", "Stripe", "Any Company"
  val triggerCondition: String, // e.g. "If email from company Zepto received"
  val triggerFilterKeywords: String = "", // e.g. "interview, assessment, offer, availability"
  val responseType: String = "AUTO_DRAFT_REPLY", // AUTO_DRAFT_REPLY, CALENDAR_FOLLOW_UP, PIPELINE_UPDATE, AI_INTERVIEW_PREP, MOBILE_ALERT, CUSTOM_AGENT_ACTION
  val automatedResponse: String, // Description of response, e.g. "Auto-draft high-conviction executive reply with availability and notify candidate"
  val responseTemplate: String = "", // Response template/body or prompt
  val targetAgent: String = "Inbound Recruiter Specialist", // Agent responsible for executing
  val requireHumanApproval: Boolean = false, // If true, holds in Pending Approvals queue before dispatch
  val isEnabled: Boolean = true,
  val executionCount: Int = 0,
  val lastTriggeredTime: String = "Never",
  val lastExecutionResult: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)
