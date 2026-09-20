package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Granular news article and strategic development for an industry sector
 * represented in the user's target companies pipeline, summarized via Gemini API.
 */
@JsonClass(generateAdapter = true)
data class SectorNewsArticle(
  @param:Json(name = "id") val id: String = "",
  @param:Json(name = "sector") val sector: String = "",
  @param:Json(name = "headline") val headline: String = "",
  @param:Json(name = "summary") val summary: String = "",
  @param:Json(name = "keyTakeaway") val keyTakeaway: String = "",
  @param:Json(name = "impactLevel") val impactLevel: String = "HIGH", // HIGH, MODERATE, STRATEGIC
  @param:Json(name = "sentiment") val sentiment: String = "BULLISH", // BULLISH, NEUTRAL, CAUTIOUS
  @param:Json(name = "source") val source: String = "Economic Times / TechCrunch",
  @param:Json(name = "timeAgo") val timeAgo: String = "Today",
  @param:Json(name = "relevantTargetCompanies") val relevantTargetCompanies: List<String> = emptyList(),
  @param:Json(name = "hiringImplication") val hiringImplication: String = "",
  @param:Json(name = "tags") val tags: List<String> = emptyList()
)

/**
 * Macro intelligence and hiring posture for a specific sector
 */
@JsonClass(generateAdapter = true)
data class SectorMacroSummary(
  @param:Json(name = "sector") val sector: String = "",
  @param:Json(name = "macroTrend") val macroTrend: String = "",
  @param:Json(name = "hiringVelocity") val hiringVelocity: String = "SURGING", // SURGING, STEADY, SELECTIVE
  @param:Json(name = "topSkillsInDemand") val topSkillsInDemand: List<String> = emptyList(),
  @param:Json(name = "keyCompaniesTracked") val keyCompaniesTracked: List<String> = emptyList(),
  @param:Json(name = "sentiment") val sentiment: String = "BULLISH"
)

/**
 * Aggregated report of sector news and market insights summarized by Gemini API
 * based on sectors represented in the user's target companies.
 */
@JsonClass(generateAdapter = true)
data class SectorMarketInsightsReport(
  @param:Json(name = "executiveSummary") val executiveSummary: String = "",
  @param:Json(name = "sectorsAnalyzed") val sectorsAnalyzed: List<String> = emptyList(),
  @param:Json(name = "sectorMacroSummaries") val sectorMacroSummaries: List<SectorMacroSummary> = emptyList(),
  @param:Json(name = "newsArticles") val newsArticles: List<SectorNewsArticle> = emptyList(),
  @param:Json(name = "generatedTimestamp") val generatedTimestamp: Long = System.currentTimeMillis(),
  @param:Json(name = "modelUsed") val modelUsed: String = "gemini-3.5-flash",
  @param:Json(name = "isGroundedWithGemini") val isGroundedWithGemini: Boolean = true
)
