package com.example.data.model

import com.example.ui.components.CareerSeniority
import com.example.ui.components.MarketTier

data class SalaryBenchmarkAnalysis(
  val targetRole: String,
  val targetCompany: String,
  val detectedTier: MarketTier,
  val detectedSeniority: CareerSeniority,
  val yearsOfExperience: Int,
  val p25BaseLakhs: Double,
  val p50BaseLakhs: Double,
  val p75BaseLakhs: Double,
  val p90BaseLakhs: Double,
  val p25TotalLakhs: Double,
  val p50TotalLakhs: Double,
  val p75TotalLakhs: Double,
  val p90TotalLakhs: Double,
  val medianBonusPercent: Int,
  val fourYearEquityLakhs: Double,
  val signOnBonusRange: String,
  val candidateLeverageScore: Int, // 0-100
  val leverageVerdict: String,     // e.g. "VERY HIGH", "STRONG", "MODERATE"
  val keyMarketDataPoints: List<String>,
  val jdAlignmentHighlights: List<String>,
  val candidateStrengthsCited: List<String>
)

data class NegotiationScriptScenario(
  val scenarioKey: String, // "INITIAL_COUNTER", "BUDGET_PUSHBACK", "COMPETING_OFFER"
  val title: String,
  val subtitle: String,
  val subjectLine: String,
  val scriptBody: String,
  val tacticalPsychologyNotes: String,
  val dataPointsCited: List<String>
)

data class PhoneCallTalkingPoints(
  val openingHook: String,
  val theTargetAnchorAsk: String,
  val objectionHandlingBudgetCeiling: String,
  val objectionHandlingInternalEquity: String,
  val closingCommitment: String
)

data class SalaryNegotiationBundle(
  val id: String = java.util.UUID.randomUUID().toString(),
  val targetRole: String,
  val targetCompany: String,
  val candidateCurrentCtcLakhs: Double,
  val recommendedTargetAnchorLakhs: Double,
  val walkawayFloorLakhs: Double,
  val benchmarkAnalysis: SalaryBenchmarkAnalysis,
  val scenarios: List<NegotiationScriptScenario>,
  val phoneCallGuide: PhoneCallTalkingPoints,
  val strategicNegotiationRules: List<String>,
  val isAiGenerated: Boolean = false,
  val generatedTimestamp: Long = System.currentTimeMillis()
)
