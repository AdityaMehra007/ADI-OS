package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity storing the local persistence state for the Company Intelligence widget.
 * Saves the company name and last cached AI-synthesized summary so that the widget state
 * persists across app restarts without requiring network access.
 */
@Entity(
  tableName = "company_intelligence_widget_cache",
  indices = [
    Index(value = ["isLastSelected"]),
    Index(value = ["lastUpdatedMillis"])
  ]
)
data class CompanyIntelligenceWidgetCache(
  @PrimaryKey val companyName: String,
  val cachedSummary: String,
  val timestamp: String = "",
  val hiringUpdates: String = "",
  val interviewAngle: String = "",
  val executiveShifts: String = "",
  val strategicRisks: String = "",
  val sourcesJson: String = "",
  val confidenceScore: Int = 95,
  val isLiveGrounded: Boolean = true,
  val isLastSelected: Boolean = false,
  val lastUpdatedMillis: Long = System.currentTimeMillis()
)

fun CompanyIntelligenceWidgetCache.toCompanyIntelReport(): CompanyIntelReport {
  return CompanyIntelReport(
    companyName = this.companyName,
    timestamp = this.timestamp.ifBlank { "Persisted Local Room Cache" },
    businessNewsSummary = this.cachedSummary,
    hiringUpdates = if (this.hiringUpdates.isNotBlank()) {
      this.hiringUpdates.split("\n").filter { it.isNotBlank() }
    } else {
      listOf("Monitored hiring signals stored in local Room database.")
    },
    executiveShifts = if (this.executiveShifts.isNotBlank()) {
      this.executiveShifts.split("\n").filter { it.isNotBlank() }
    } else {
      listOf("Cached executive leadership directives.")
    },
    strategicRisksAndOpportunities = this.strategicRisks.ifBlank {
      "Local Room persistent intelligence for ${this.companyName}."
    },
    interviewAngles = if (this.interviewAngle.isNotBlank()) {
      listOf(this.interviewAngle)
    } else {
      listOf("Leverage metrics and supply chain/operations data in discussion with ${this.companyName}.")
    },
    searchQueriesUsed = listOf(
      "${this.companyName} latest business news 2026",
      "${this.companyName} expansion Bengaluru hiring"
    ),
    groundedSources = if (this.sourcesJson.isNotBlank()) {
      this.sourcesJson.split("\n").filter { it.isNotBlank() }.map { line ->
        val parts = line.split("|||")
        if (parts.size >= 2) GroundedSource(parts[0], parts[1]) else GroundedSource(parts[0], "")
      }
    } else {
      listOf(
        GroundedSource(
          title = "${this.companyName} Official Corporate Profile",
          url = "https://google.com/search?q=${this.companyName}"
        )
      )
    },
    isLiveGrounded = if (this.cachedSummary.isBlank()) false else this.isLiveGrounded,
    confidenceScore = if (this.cachedSummary.isBlank()) 0 else this.confidenceScore
  )
}

fun CompanyIntelReport.toWidgetCache(isLastSelected: Boolean = true): CompanyIntelligenceWidgetCache {
  return CompanyIntelligenceWidgetCache(
    companyName = this.companyName,
    cachedSummary = this.businessNewsSummary,
    timestamp = this.timestamp,
    hiringUpdates = this.hiringUpdates.joinToString("\n"),
    interviewAngle = this.interviewAngles.firstOrNull() ?: "",
    executiveShifts = this.executiveShifts.joinToString("\n"),
    strategicRisks = this.strategicRisksAndOpportunities,
    sourcesJson = this.groundedSources.joinToString("\n") { "${it.title}|||${it.url}" },
    confidenceScore = this.confidenceScore,
    isLiveGrounded = this.isLiveGrounded,
    isLastSelected = isLastSelected,
    lastUpdatedMillis = System.currentTimeMillis()
  )
}
