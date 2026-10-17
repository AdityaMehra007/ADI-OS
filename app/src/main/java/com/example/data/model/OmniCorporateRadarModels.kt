package com.example.data.model

/**
 * Sector classifications for corporate tracking.
 */
enum class CorporateSector(val label: String, val badgeTitle: String) {
  ALL("All Sectors", "ALL"),
  TECH_MNC("Tech MNCs", "MNC"),
  STARTUP("High-Growth Startups", "STARTUP"),
  UNICORN("Quick-Commerce & Unicorns", "UNICORN"),
  CONSULTING("Strategy Consulting", "CONSULTING"),
  FINTECH("Fintech & Banking", "FINTECH"),
  COMMERCE("Fulfillment & Retail", "COMMERCE")
}

/**
 * Functional departments for cross-departmental hiring tracking.
 */
enum class CorporateDepartment(val label: String, val code: String) {
  ALL("All Roles", "ALL"),
  STRATEGY_OPS("Strategy, Ops & CoS", "STRAT"),
  ENGINEERING_AI("Engineering & AI", "ENG"),
  PRODUCT_DESIGN("Product & Design", "PROD"),
  FINANCE_BIZOPS("Finance & BizOps", "FIN"),
  PEOPLE_TALENT("People & Talent", "HR"),
  COMMERCE("Fulfillment & Commerce Ops", "OPS")
}

/**
 * Recency filtering for corporate updates.
 */
enum class RoleFreshness(val label: String) {
  ALL("All Active"),
  TODAY("New Today 🔥"),
  PAST_48_HOURS("Past 48 Hours"),
  THIS_WEEK("This Week")
}

/**
 * Data model for a daily company update and open role opportunity.
 */
data class CorporateDailyUpdateItem(
  val id: String,
  val companyName: String,
  val sector: CorporateSector,
  val roleTitle: String,
  val department: CorporateDepartment,
  val location: String,
  val compensationRange: String,
  val postedDate: String = "Today",
  val isNewToday: Boolean = true,
  val hiringUrgency: String = "HIGH_PRIORITY", // "IMMEDIATE", "HIGH_PRIORITY", "EXPANSION_WAVE"
  val headlineUpdate: String,
  val keyRequirements: List<String> = emptyList(),
  val hiringManagerLead: String = "Talent Acquisition Lead",
  val applyUrl: String = "",
  val isSavedToLedger: Boolean = false,
  val ledgerStatus: String? = null,
  val groundedSources: List<GroundedSource> = emptyList(),
  val experienceLevel: String = "Senior / Lead (3-7 yrs)",
  val ctcLakhsEstimate: Int = 32
)
