package com.example.data.model

import com.squareup.moshi.JsonClass
import java.util.UUID

/**
 * Structured talking point item representing an executive pitch,
 * quantifiable proof point, company-specific strategic angle, or STAR framing.
 */
@JsonClass(generateAdapter = true)
data class StructuredTalkingPoint(
  val id: String = UUID.randomUUID().toString(),
  val pillar: String = "STRATEGIC_ALIGNMENT", // EXECUTIVE_HOOK, METRICS_PROOF, STRATEGIC_ALIGNMENT, STAR_FRAMEWORK, PITFALLS_AVOID
  val title: String,
  val bulletPoints: List<String> = emptyList(),
  val impactMetric: String = "",
  val strategicRationale: String = ""
)

/**
 * Role-specific mock interview question generated based on selected company and job title.
 */
@JsonClass(generateAdapter = true)
data class InterviewCommandQuestion(
  val id: String = UUID.randomUUID().toString(),
  val questionText: String,
  val category: String = "BEHAVIORAL_STAR", // BEHAVIORAL_STAR, ANALYTICAL_CASE, STRATEGIC_FIT, ROLE_DOMAIN, BAR_RAISER
  val difficulty: String = "CORE", // CORE, ADVANCED, BAR_RAISER
  val whyCompanyAsksThis: String = "",
  val candidateProfileAnchor: String = "",
  val structuredTalkingPoints: List<String> = emptyList(),
  val starSituation: String = "",
  val starTask: String = "",
  val starAction: String = "",
  val starResult: String = "",
  val sampleStarAnswer: String = "",
  val pitfallsToAvoid: List<String> = emptyList()
)

/**
 * Complete Interview Command Dossier generated for a specific job title and company from Application Hub.
 */
@JsonClass(generateAdapter = true)
data class InterviewCommandDossier(
  val id: String = UUID.randomUUID().toString(),
  val companyName: String,
  val jobTitle: String,
  val industry: String = "Technology & Enterprise SaaS",
  val executiveContext: String = "",
  val candidateMappingRationale: String = "",
  val strategicTalkingPoints: List<StructuredTalkingPoint> = emptyList(),
  val mockQuestions: List<InterviewCommandQuestion> = emptyList(),
  val highImpactQuestionsForInterviewer: List<String> = emptyList(),
  val generatedTimestamp: Long = System.currentTimeMillis(),
  val modelUsed: String = "gemini-3.5-flash",
  val isGroundedWithGemini: Boolean = true
)
