package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Room Database Entity to store and version user resume text.
 * Enables users to manage multiple resume variations (e.g. Zepto Ops vs Razorpay FinTech),
 * track version evolution (v1.0 -> v2.0), branch revisions, and record change notes.
 */
@Entity(tableName = "resume_variations")
data class ResumeVariation(
  @PrimaryKey val id: String = UUID.randomUUID().toString(),
  val title: String,
  val targetCompany: String = "General",
  val targetRole: String = "Operations Strategy Lead",
  val resumeText: String,
  val versionNumber: Int = 1,
  val versionLabel: String = "v1.0",
  val versionNotes: String = "Initial baseline version",
  val isPrimary: Boolean = false,
  val parentVariationId: String? = null,
  val atsCompatibilityScore: Int = 85,
  val overallMatchScore: Int = 88,
  val tags: String = "Operations, Strategy, Analytics",
  val wordCount: Int = 0,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis()
)
