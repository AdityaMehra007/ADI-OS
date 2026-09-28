package com.example.data.repository

import com.example.data.dao.CareerTelemetryDao
import com.example.data.dao.InterviewFeedbackDao
import com.example.data.model.CareerTelemetry
import com.example.data.model.InterviewFeedback
import com.example.data.model.TelemetryMetricType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository pattern implementation for Career Telemetry and Interview Feedback history.
 * Decouples database DAOs from UI and ViewModels.
 */
class CareerTelemetryRepository(
  private val telemetryDao: CareerTelemetryDao,
  private val feedbackDao: InterviewFeedbackDao
) {

  // Reactive Telemetry Flows
  val allTelemetryFlow: Flow<List<CareerTelemetry>> = telemetryDao.getAllTelemetryFlow()
  val telemetryCountFlow: Flow<Int> = telemetryDao.getTelemetryCountFlow()

  fun getRecentTelemetryFlow(limit: Int = 10): Flow<List<CareerTelemetry>> =
    telemetryDao.getRecentTelemetryFlow(limit)

  fun getTelemetryForCompanyFlow(companyName: String): Flow<List<CareerTelemetry>> =
    telemetryDao.getTelemetryForCompanyFlow(companyName)

  fun getTelemetryByTypeFlow(metricType: String): Flow<List<CareerTelemetry>> =
    telemetryDao.getTelemetryByTypeFlow(metricType)

  // Reactive Interview Feedback Flows
  val allFeedbackFlow: Flow<List<InterviewFeedback>> = feedbackDao.getAllFeedbackFlow()
  val averageScoreFlow: Flow<Float?> = feedbackDao.getAverageScoreFlow()
  val highestScoreFlow: Flow<Int?> = feedbackDao.getHighestScoreFlow()
  val totalInterviewCountFlow: Flow<Int> = feedbackDao.getTotalInterviewCountFlow()

  fun getFeedbackForCompanyFlow(companyName: String): Flow<List<InterviewFeedback>> =
    feedbackDao.getFeedbackForCompanyFlow(companyName)

  fun getFeedbackForApplicationFlow(applicationId: String): Flow<List<InterviewFeedback>> =
    feedbackDao.getFeedbackForApplicationFlow(applicationId)

  fun getRecentFeedbackFlow(limit: Int = 5): Flow<List<InterviewFeedback>> =
    feedbackDao.getRecentFeedbackFlow(limit)

  // Suspend Mutator Operations
  suspend fun recordTelemetry(item: CareerTelemetry) {
    telemetryDao.insertTelemetry(item)
  }

  suspend fun recordApplicationStatusChange(
    companyName: String,
    roleTitle: String,
    previousStatus: String,
    newStatus: String,
    details: String = "",
    score: Int = 0,
    source: String = "SYSTEM_AUTOMATION"
  ): CareerTelemetry {
    val telemetry = CareerTelemetry(
      id = UUID.randomUUID().toString(),
      timestamp = System.currentTimeMillis(),
      metricType = TelemetryMetricType.APPLICATION_STATUS_CHANGE.name,
      companyName = companyName,
      roleTitle = roleTitle,
      previousStatus = previousStatus,
      newStatus = newStatus,
      details = details.ifBlank { "Application status transitioned from $previousStatus to $newStatus." },
      score = score,
      source = source
    )
    telemetryDao.insertTelemetry(telemetry)
    return telemetry
  }

  suspend fun recordInterviewFeedback(feedback: InterviewFeedback) {
    feedbackDao.insertFeedback(feedback)
    
    // Also record a corresponding telemetry event for holistic tracking
    val telemetry = CareerTelemetry(
      id = UUID.randomUUID().toString(),
      timestamp = feedback.interviewDate,
      metricType = if (feedback.isMockSimulation) TelemetryMetricType.MOCK_INTERVIEW_SESSION.name else TelemetryMetricType.INTERVIEW_COMPLETED.name,
      companyName = feedback.companyName,
      roleTitle = feedback.targetRole,
      previousStatus = "INTERVIEW_SCHEDULED",
      newStatus = feedback.outcome,
      details = "Completed ${feedback.roundType} interview. Overall Score: ${feedback.overallScore}/100. Evaluator notes: ${feedback.evaluatorNotes}",
      score = feedback.overallScore,
      source = if (feedback.isMockSimulation) "INTERVIEW_SIMULATOR" else "USER_INPUT"
    )
    telemetryDao.insertTelemetry(telemetry)
  }

  suspend fun deleteTelemetryById(id: String) {
    telemetryDao.deleteTelemetryById(id)
  }

  suspend fun deleteFeedbackById(id: String) {
    feedbackDao.deleteFeedbackById(id)
  }

  suspend fun clearAllTelemetry() {
    telemetryDao.clearAllTelemetry()
  }

  suspend fun clearAllFeedback() {
    feedbackDao.clearAllFeedback()
  }
}
