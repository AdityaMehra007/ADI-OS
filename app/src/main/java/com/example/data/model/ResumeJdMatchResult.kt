package com.example.data.model

import com.example.ui.components.MatchTier

/**
 * Result of comparing a parsed resume (PDF or text) against a job description.
 */
data class ResumeJdMatchResult(
  val overallMatchScore: Int, // 0..100
  val skillsMatchScore: Int, // 0..100
  val experienceFitScore: Int, // 0..100
  val metricsScore: Int, // 0..100
  val matchedSkills: List<String>,
  val missingSkills: List<String>,
  val totalJdSkillsCount: Int,
  val matchedCount: Int,
  val executiveVerdict: String,
  val strengths: List<String>,
  val gapPivots: List<String>,
  val parsedResumeName: String,
  val parsedResumeRole: String,
  val targetRole: String,
  val targetCompany: String,
  val matchTier: MatchTier = MatchTier.fromScore(overallMatchScore)
)
