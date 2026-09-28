package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Enums representing the specific types of Career Telemetry events tracked by the system.
 */
enum class TelemetryMetricType(val displayName: String, val category: String) {
  APPLICATION_STATUS_CHANGE("Application Status Change", "PIPELINE"),
  INTERVIEW_COMPLETED("Interview Completed", "INTERVIEW"),
  MOCK_INTERVIEW_SESSION("Mock Interview Session", "TRAINING"),
  OFFER_RECEIVED("Offer Received", "OUTCOME"),
  SLA_COMPLIANCE_RECORD("SLA Compliance Audit", "GOVERNANCE"),
  PIPELINE_VELOCITY_UPDATE("Pipeline Velocity Delta", "METRICS"),
  OUTREACH_DISPATCHED("Outreach InMail Dispatched", "OUTREACH")
}

/**
 * Represents structured career telemetry tracking state changes, velocity metrics,
 * and operational milestones for job opportunities.
 */
@Entity(
  tableName = "career_telemetry",
  indices = [
    Index(value = ["timestamp"]),
    Index(value = ["metricType"]),
    Index(value = ["companyName"])
  ]
)
data class CareerTelemetry(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val timestamp: Long = System.currentTimeMillis(),
  val metricType: String = TelemetryMetricType.APPLICATION_STATUS_CHANGE.name,
  val companyName: String,
  val roleTitle: String,
  val previousStatus: String = "",
  val newStatus: String = "",
  val details: String = "",
  val score: Int = 0,
  val source: String = "MANUAL_ENTRY", // SYSTEM_AUTOMATION, USER_INPUT, INTERVIEW_SIMULATOR
  val metadataJson: String = "{}"
)

/**
 * Detailed interview feedback and evaluation history record.
 * Stores granular STAR dimension scoring, interviewer notes, and executive rephrases.
 */
@Entity(
  tableName = "interview_feedback_history",
  indices = [
    Index(value = ["interviewDate"]),
    Index(value = ["companyName"]),
    Index(value = ["applicationId"])
  ]
)
data class InterviewFeedback(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val applicationId: String = "",
  val companyName: String,
  val targetRole: String,
  val roundType: String = "OPERATIONS_SIMULATION", // RECRUITER_SCREEN, TECHNICAL_CASE, OPERATIONS_SIMULATION, VP_EXECUTIVE
  val interviewerRole: String = "VP of Operations",
  val interviewDate: Long = System.currentTimeMillis(),
  val overallScore: Int = 85, // 0 - 100
  val situationClarityScore: Int = 85,
  val actionImpactScore: Int = 90,
  val resultMetricsScore: Int = 88,
  val keyStrengths: String = "", // Comma-separated or bullet notes
  val areasForImprovement: String = "",
  val keyQuestionsAsked: String = "",
  val candidateResponseSummary: String = "",
  val executiveRephraseRecommendation: String = "",
  val outcome: String = "PASSED", // PASSED, ADVANCED, OFFER_EXTENDED, PENDING, REJECTED
  val evaluatorNotes: String = "",
  val isMockSimulation: Boolean = true,
  val timestamp: Long = System.currentTimeMillis()
)
