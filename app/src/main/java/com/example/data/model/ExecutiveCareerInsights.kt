package com.example.data.model

/**
 * High-signal pillar evaluation inside the Executive Command Center
 */
data class ExecutiveInsightPillar(
  val id: String,
  val title: String,
  val score: Int, // 0..100
  val benchmarkTier: String, // e.g. "Top 3% Talent", "Top 5% Bengaluru Tech"
  val keyStrength: String,
  val strategicImperative: String
)

/**
 * Top high-yield market match synthesized by Gemini
 */
data class ExecutiveMarketOpportunity(
  val companyName: String,
  val targetRole: String,
  val compensationRange: String,
  val fitScore: Int, // 0..100
  val leverageThesis: String,
  val hiringVelocity: String // "SURGING", "ACTIVE", "HIGH_PRIORITY"
)

/**
 * Daily Career Insight Tip for personalized high-leverage guidance
 */
data class DailyCareerTip(
  val id: String,
  val category: String, // e.g. "TACTICAL NEGOTIATION", "OPERATIONAL MOAT", "AI ORCHESTRATION", "EXECUTIVE PRESENCE"
  val headline: String,
  val tipText: String,
  val actionableTag: String,
  val iconType: String = "BOLT" // "BOLT", "PSYCHOLOGY", "ROCKET", "MONETIZATION", "STAR"
)

/**
 * Complete Executive Career Insights dossier synthesized by the Gemini API
 */
data class ExecutiveCareerInsights(
  val headline: String,
  val executiveVerdict: String,
  val marketPositioningStatement: String,
  val targetCompensationRange: String,
  val strategicPillars: List<ExecutiveInsightPillar>,
  val topMarketOpportunities: List<ExecutiveMarketOpportunity>,
  val competitiveMoats: List<String>,
  val criticalBlindspots: List<String>,
  val immediate30DayPriorities: List<String>,
  val interviewPreparationVectors: List<String>,
  val dailyTips: List<DailyCareerTip> = emptyList(),
  val overallReadinessScore: Int = 94,
  val confidenceScore: Int = 96,
  val modelUsed: String = "gemini-3.5-flash",
  val timestamp: String = "Just now",
  val isLiveApi: Boolean = true
)

/**
 * UI State for the Executive Command Center Screen
 */
data class ExecutiveCommandCenterUiState(
  val isLoading: Boolean = false,
  val insights: ExecutiveCareerInsights? = null,
  val error: String? = null,
  val targetRoleFilter: String = "Lead Strategy & Operations / Chief of Staff",
  val activeTab: Int = 0 // 0: Executive Briefing, 1: Strategic Pillars, 2: Market Opportunities, 3: Tactical Playbook
)
