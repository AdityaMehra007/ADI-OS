package com.example.data.model

/**
 * Types of intelligence feed entries returned by Google Search Grounding.
 */
enum class IntelligenceFeedItemType(val label: String, val badge: String) {
  ALL("All Intel", "ALL"),
  CORPORATE_NEWS("Corporate News", "NEWS"),
  INDUSTRY_TREND("Industry Trends", "TREND"),
  JOB_OPENING("Job Openings", "ROLE")
}

/**
 * Prioritized sectors requested for daily tracking.
 */
enum class PrioritizedSector(val label: String, val code: String) {
  ALL("All Sectors", "ALL"),
  TECH("Tech (MNCs & Startups)", "TECH"),
  CONSULTING("Strategy Consulting", "CONSULTING"),
  FINTECH("Fintech & Banking", "FINTECH"),
  COMMERCE("Quick-Commerce & Retail", "COMMERCE")
}

/**
 * Represents an individual grounded item in the Daily Intelligence Feed.
 */
data class IntelligenceFeedItem(
  val id: String,
  val type: IntelligenceFeedItemType,
  val sector: PrioritizedSector,
  val headline: String,
  val companyName: String,
  val details: String,
  val keyTags: List<String> = emptyList(),
  val roleTitle: String? = null,
  val location: String? = null,
  val compensationRange: String? = null,
  val ctcLakhsEstimate: Int? = null,
  val hiringUrgency: String? = null, // "IMMEDIATE", "HIGH_PRIORITY", "EXPANSION_WAVE"
  val publishedTime: String = "Today",
  val isBreaking: Boolean = true,
  val impactScore: Int = 92,
  val groundedSources: List<GroundedSource> = emptyList(),
  val isSavedToLedger: Boolean = false,
  val ledgerStatus: String? = null,
  val applyUrl: String = ""
)

/**
 * Encapsulates the complete daily intelligence briefing synthesized via Google Search Grounding.
 */
data class DailyIntelligenceFeedReport(
  val generatedTimestamp: Long = System.currentTimeMillis(),
  val formattedDate: String = "Today",
  val groundingWebQueries: List<String> = emptyList(),
  val totalNewsCount: Int = 0,
  val totalTrendsCount: Int = 0,
  val totalOpeningsCount: Int = 0,
  val feedItems: List<IntelligenceFeedItem> = emptyList(),
  val executiveSectorTakeaway: String = "",
  val groundingEngineStatus: String = "GOOGLE SEARCH GROUNDING ACTIVE"
)
