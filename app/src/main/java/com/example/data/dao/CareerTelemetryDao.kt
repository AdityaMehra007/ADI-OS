package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CareerTelemetry
import com.example.data.model.InterviewFeedback
import kotlinx.coroutines.flow.Flow

@Dao
interface CareerTelemetryDao {

  @Query("SELECT * FROM career_telemetry ORDER BY timestamp DESC")
  fun getAllTelemetryFlow(): Flow<List<CareerTelemetry>>

  @Query("SELECT * FROM career_telemetry WHERE metricType = :metricType ORDER BY timestamp DESC")
  fun getTelemetryByTypeFlow(metricType: String): Flow<List<CareerTelemetry>>

  @Query("SELECT * FROM career_telemetry WHERE companyName = :companyName ORDER BY timestamp DESC")
  fun getTelemetryForCompanyFlow(companyName: String): Flow<List<CareerTelemetry>>

  @Query("SELECT * FROM career_telemetry ORDER BY timestamp DESC LIMIT :limit")
  fun getRecentTelemetryFlow(limit: Int): Flow<List<CareerTelemetry>>

  @Query("SELECT * FROM career_telemetry ORDER BY timestamp DESC")
  suspend fun getAllTelemetrySnapshot(): List<CareerTelemetry>

  @Query("SELECT * FROM career_telemetry WHERE id = :id LIMIT 1")
  suspend fun getTelemetryById(id: String): CareerTelemetry?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTelemetry(item: CareerTelemetry)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(items: List<CareerTelemetry>)

  @Query("DELETE FROM career_telemetry WHERE id = :id")
  suspend fun deleteTelemetryById(id: String)

  @Query("DELETE FROM career_telemetry")
  suspend fun clearAllTelemetry()

  @Query("SELECT COUNT(*) FROM career_telemetry")
  fun getTelemetryCountFlow(): Flow<Int>
}

@Dao
interface InterviewFeedbackDao {

  @Query("SELECT * FROM interview_feedback_history ORDER BY interviewDate DESC")
  fun getAllFeedbackFlow(): Flow<List<InterviewFeedback>>

  @Query("SELECT * FROM interview_feedback_history WHERE companyName = :companyName ORDER BY interviewDate DESC")
  fun getFeedbackForCompanyFlow(companyName: String): Flow<List<InterviewFeedback>>

  @Query("SELECT * FROM interview_feedback_history WHERE applicationId = :applicationId ORDER BY interviewDate DESC")
  fun getFeedbackForApplicationFlow(applicationId: String): Flow<List<InterviewFeedback>>

  @Query("SELECT * FROM interview_feedback_history WHERE id = :id LIMIT 1")
  suspend fun getFeedbackById(id: String): InterviewFeedback?

  @Query("SELECT * FROM interview_feedback_history ORDER BY interviewDate DESC LIMIT :limit")
  fun getRecentFeedbackFlow(limit: Int): Flow<List<InterviewFeedback>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFeedback(feedback: InterviewFeedback)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(feedbacks: List<InterviewFeedback>)

  @Update
  suspend fun updateFeedback(feedback: InterviewFeedback)

  @Query("DELETE FROM interview_feedback_history WHERE id = :id")
  suspend fun deleteFeedbackById(id: String)

  @Query("DELETE FROM interview_feedback_history")
  suspend fun clearAllFeedback()

  @Query("SELECT AVG(overallScore) FROM interview_feedback_history")
  fun getAverageScoreFlow(): Flow<Float?>

  @Query("SELECT MAX(overallScore) FROM interview_feedback_history")
  fun getHighestScoreFlow(): Flow<Int?>

  @Query("SELECT COUNT(*) FROM interview_feedback_history")
  fun getTotalInterviewCountFlow(): Flow<Int>
}
