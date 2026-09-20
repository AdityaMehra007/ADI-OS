package com.example.data.model

/**
 * Directional movement of company stock or valuation
 */
enum class StockTrendDirection(val label: String, val isPositive: Boolean) {
  STRONG_UP("Strong Bullish", true),
  UP("Upward Trend", true),
  NEUTRAL("Consolidating", true),
  DOWN("Pullback", false),
  VOLATILE("High Volatility", false)
}

/**
 * Velocity of hiring shifts in target companies
 */
enum class HiringShiftDirection(val label: String, val badgeColorHex: Long) {
  SURGING("SURGING VELOCITY", 0xFF10B981),
  EXPANDING_BENGALURU("BENGALURU EXPANSION", 0xFF00E5FF),
  SELECTIVE_STRATEGIC("SELECTIVE / HIGH BAR", 0xFFF59E0B),
  CONSOLIDATING("ROLE SPECIALIZATION", 0xFF6366F1),
  STEADY("STABLE HIRING", 0xFF94A3B8)
}

/**
 * Briefing for an individual target company
 */
data class GroundedCompanyBriefing(
  val companyId: String,
  val companyName: String,
  val logoEmoji: String,
  val industry: String,
  val tickerSymbol: String,
  val stockPrice: String,
  val stockTrendDelta: String,
  val stockTrendDirection: StockTrendDirection,
  val stockTrendRationale: String,
  val newsHeadline: String,
  val newsSummary: String,
  val hiringShiftLabel: String,
  val hiringShiftDirection: HiringShiftDirection,
  val hiringShiftsSummary: String,
  val keyInDemandRoles: List<String>,
  val strategicInterviewAngle: String,
  val groundedSources: List<GroundedSource> = emptyList(),
  val activeOpeningsCount: Int = 0,
  val confidenceScore: Int = 96
)

/**
 * Comprehensive Daily Career Briefing dossier grounded in Google Search
 */
data class DailyCareerBriefingReport(
  val briefingId: String,
  val dateFormatted: String,
  val timestampFormatted: String,
  val macroMarketSummary: String,
  val overallMarketSentiment: String,
  val topFiveCompanies: List<GroundedCompanyBriefing>,
  val searchQueriesUsed: List<String>,
  val isLiveGoogleSearchGrounded: Boolean,
  val groundingStatusBadge: String,
  val totalSourcesCount: Int = 0,
  val latencyMs: Long = 0L
)
