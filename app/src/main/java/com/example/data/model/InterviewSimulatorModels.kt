package com.example.data.model

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class GeneratedInterviewQuestion(
  val id: String = UUID.randomUUID().toString(),
  val questionText: String,
  val category: String = "BEHAVIORAL", // BEHAVIORAL, ANALYTICAL_CASE, STRATEGY_FIT, CURVEBALL_PRESSURE, ROLE_SPECIFIC
  val difficulty: String = "CORE", // WARM_UP, CORE, ADVANCED, EXECUTIVE
  val whyCompanyAsksThis: String = "",
  val candidateProfileAnchor: String = "",
  val keyPointsToHit: List<String> = emptyList(),
  val sampleStarAnswer: String = "",
  val isPracticed: Boolean = false,
  val candidateLastScore: Int? = null,
  val feedbackSummary: String? = null
)

@JsonClass(generateAdapter = true)
data class CompanyInterviewSimulationDossier(
  val companyName: String,
  val targetRole: String,
  val industry: String = "Technology",
  val roundType: String = "ALL_ROUNDS", // RECRUITER_SCREEN, HIRING_MANAGER, ANALYTICS_CASE, VP_EXECUTIVE
  val executiveContext: String = "",
  val candidateGroundingSummary: String = "",
  val questions: List<GeneratedInterviewQuestion> = emptyList(),
  val generatedTimestamp: Long = System.currentTimeMillis(),
  val modelUsed: String = "gemini-3.5-flash",
  val isGroundedWithGemini: Boolean = true
)

@JsonClass(generateAdapter = true)
data class InterviewAnswerEvaluation(
  val score: Int = 85,
  val situationClarity: String = "",
  val taskClarity: String = "",
  val actionImpact: String = "",
  val resultMetrics: String = "",
  val keyStrengths: List<String> = emptyList(),
  val areasForImprovement: List<String> = emptyList(),
  val polishedExecutiveRephrase: String = "",
  val evaluatorNotes: String = ""
)
