package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room SQLite Entity for Application Pipeline company cards.
 * Provides offline-first persistence on-device for tracking corporate application
 * statuses ('Applied', 'Interviewing', 'Offer', 'Targeted'), roles, compensations,
 * interview stages, and notes when no internet connectivity is available.
 */
@Entity(
  tableName = "application_pipeline_companies",
  indices = [
    Index(value = ["status"]),
    Index(value = ["isStarred"]),
    Index(value = ["lastUpdated"])
  ]
)
data class ApplicationPipelineCompanyEntity(
  @PrimaryKey val companyName: String,
  val status: String = "Applied", // "Targeted", "Applied", "Interviewing", "Offer", "Archived"
  val role: String = "Operations & Strategy Lead",
  val compensation: String = "₹28L - ₹34L CTC",
  val interviewStage: String = "Initial Screening",
  val contactLead: String = "Executive Referral",
  val notes: String = "",
  val lastUpdated: String = "",
  val isStarred: Boolean = true,
  val offlineCachedTimestamp: Long = System.currentTimeMillis()
)
