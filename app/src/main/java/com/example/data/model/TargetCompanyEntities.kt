package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Room entity representing a tracked Target Company in the user's career portfolio.
 */
@Entity(
  tableName = "target_companies",
  indices = [
    Index(value = ["priorityTier"]),
    Index(value = ["trackingStatus"]),
    Index(value = ["name"])
  ]
)
data class TargetCompany(
  @PrimaryKey val id: String,
  val name: String,
  val tickerOrDomain: String = "",
  val logoEmoji: String = "🏢",
  val industry: String = "Technology",
  val subIndustry: String = "Enterprise SaaS & AI",
  val hqLocation: String = "Bengaluru, India",
  val bengaluruHub: String = "Outer Ring Road / Bellandur",
  val priorityTier: String = "TIER_1_DREAM", // TIER_1_DREAM, TIER_2_STRATEGIC, TIER_3_WATCHLIST
  val trackingStatus: String = "ACTIVE_TARGET", // EXPLORING, ACTIVE_TARGET, NETWORKING, APPLICATION_STAGED, INTERVIEWING, OFFER_STAGE
  val targetRoleTitle: String = "Associate Business Analyst",
  val targetCompensationRange: String = "₹18,00,000 - ₹26,00,000 CTC",
  val culturalFitScore: Int = 92, // 0-100
  val hiringVelocity: String = "SURGING", // SURGING, STEADY, SELECTIVE, FREEZE
  val targetQuarter: String = "Q3 2026",
  val websiteUrl: String = "",
  val careersUrl: String = "",
  val notes: String = "",
  val isWatched: Boolean = true,
  val createdAtTimestamp: Long = System.currentTimeMillis(),
  val updatedAtTimestamp: Long = System.currentTimeMillis()
)

/**
 * Room entity storing granular market intelligence, strategic news, hiring signals,
 * and interview angles for a Target Company.
 */
@Entity(
  tableName = "company_market_intelligence",
  indices = [
    Index(value = ["targetCompanyId"]),
    Index(value = ["marketSentiment"])
  ]
)
data class TargetCompanyMarketIntel(
  @PrimaryKey val id: String,
  val targetCompanyId: String,
  val companyName: String,
  val headlineSummary: String,
  val marketSentiment: String = "BULLISH", // BULLISH, NEUTRAL, CAUTIOUS
  val sentimentScore: Int = 88, // 0-100
  val strategicExpansionSignals: String = "",
  val executiveLeadershipShifts: String = "",
  val hiringTrends: String = "",
  val financialHealthAndGrowth: String = "",
  val competitiveMoat: String = "",
  val risksAndHeadwinds: String = "",
  val interviewTalkingPoints: String = "",
  val confidenceRating: Int = 95, // 0-100
  val sourceGrounding: String = "Live Market Analysis & Intelligence Feeds",
  val lastRefreshedTimestamp: Long = System.currentTimeMillis()
)

/**
 * One-to-Many relational POJO connecting a Target Company with its market intelligence history.
 */
data class TargetCompanyWithMarketIntelligence(
  @Embedded val company: TargetCompany,
  @Relation(
    parentColumn = "id",
    entityColumn = "targetCompanyId"
  )
  val intelligenceHistory: List<TargetCompanyMarketIntel> = emptyList()
) {
  val latestIntel: TargetCompanyMarketIntel?
    get() = intelligenceHistory.maxByOrNull { it.lastRefreshedTimestamp }
}
