package com.example.data.model

import java.util.UUID

/**
 * Data structures for Gemini-powered Job Description Action Plans.
 */
data class JobActionPlan(
  val id: String = UUID.randomUUID().toString(),
  val targetRole: String,
  val targetCompany: String,
  val jobDescription: String,
  val timestamp: Long = System.currentTimeMillis(),
  val matchFitScore: Int, // 0-100%
  val roleMissionSummary: String,
  val seniorityLevel: String, // e.g. "Associate / Mid-Level (0-2 Yrs Fast-Track)"
  val compensationBenchmark: String, // e.g. "₹14L – ₹22L CTC + Equity"
  val strategicPriorities: List<String>,
  val requiredSkills: List<ActionPlanSkill>,
  val interviewTalkingPoints: List<InterviewTalkingPoint>,
  val questionsToAsk: List<InterviewQuestionToAsk>,
  val trapQuestions: List<InterviewTrapQuestion>,
  val roadmapPhases: List<ActionRoadmapPhase>,
  val experienceBulletRewrites: List<ExperienceBulletRewrite> = emptyList(),
  val isSaved: Boolean = false,
  val isGeminiGenerated: Boolean = true
)

/**
 * Specific, actionable experience description bullet rewrite suggested by AI
 * to maximize alignment with the targeted role's requirements.
 */
data class ExperienceBulletRewrite(
  val id: String = UUID.randomUUID().toString(),
  val targetExperienceArea: String, // e.g. "Dark Store Operations & Fulfillment", "Telemetry & Analytics"
  val originalBullet: String, // The weak or passive original bullet
  val suggestedRewriteBullet: String, // The AI-tailored high-impact power bullet
  val targetedJdRequirement: String, // Specific requirement or mandate from target JD
  val metricInfused: String, // The quantified proof metric or benchmark infused
  val keywordsInjected: List<String> = emptyList(), // Injected ATS keywords & domain terms
  val strategicRationale: String, // Why this rewrite shifts candidate perception for this role
  val isApplied: Boolean = false
)

data class ActionPlanSkill(
  val name: String,
  val category: String, // "Technical / Analytics", "Strategic Operations", "Leadership & Presence"
  val importance: SkillImportance,
  val candidateMatchStatus: CandidateMatchStatus,
  val whyDemanded: String,
  val upskillingAction: String
)

enum class SkillImportance {
  CRITICAL,
  HIGH,
  FOUNDATIONAL
}

enum class CandidateMatchStatus {
  STRONG_MATCH,
  PARTIAL_GAP,
  CRITICAL_GAP
}

data class InterviewTalkingPoint(
  val id: String = UUID.randomUUID().toString(),
  val theme: String,
  val jdMandate: String,
  val executiveNarrative: String,
  val starBreakdown: StarMethodBreakdown,
  val proofMetricOrFact: String
)

data class StarMethodBreakdown(
  val situation: String,
  val task: String,
  val action: String,
  val result: String
)

data class InterviewQuestionToAsk(
  val id: String = UUID.randomUUID().toString(),
  val targetInterviewer: String, // "VP / Head of Operations", "Hiring Manager", "Peer Analyst"
  val question: String,
  val strategicIntent: String
)

data class InterviewTrapQuestion(
  val id: String = UUID.randomUUID().toString(),
  val question: String,
  val trapReason: String,
  val recommendedPivot: String
)

data class ActionRoadmapPhase(
  val phaseName: String,
  val timeline: String, // "Next 48 Hours", "Days 3 to 5", "Interview Eve"
  val tasks: List<ActionPlanTask>
)

data class ActionPlanTask(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val detail: String,
  val isCompleted: Boolean = false
)

data class SampleJobDescription(
  val id: String,
  val company: String,
  val role: String,
  val salary: String,
  val description: String,
  val keyHighlights: List<String>
)
