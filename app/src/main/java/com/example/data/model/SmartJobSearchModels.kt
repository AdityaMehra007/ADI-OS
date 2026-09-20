package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmartJobMatch(
  @param:Json(name = "id") val id: String,
  @param:Json(name = "jobId") val jobId: String,
  @param:Json(name = "companyId") val companyId: String,
  @param:Json(name = "companyName") val companyName: String,
  @param:Json(name = "roleTitle") val roleTitle: String,
  @param:Json(name = "roleFamily") val roleFamily: String,
  @param:Json(name = "location") val location: String,
  @param:Json(name = "experienceRequirement") val experienceRequirement: String,
  @param:Json(name = "salaryRange") val salaryRange: String,
  @param:Json(name = "jobSummary") val jobSummary: String,
  @param:Json(name = "fitScore") val fitScore: Int, // 0 - 100
  @param:Json(name = "matchVerdict") val matchVerdict: String, // e.g. "TOP MATCH - HIGH FIT", "EXCELLENT ALIGNMENT", "STRATEGIC STRETCH"
  @param:Json(name = "whyItFits") val whyItFits: String,
  @param:Json(name = "keyMatchingSkills") val keyMatchingSkills: List<String> = emptyList(),
  @param:Json(name = "missingGaps") val missingGaps: List<String> = emptyList(),
  @param:Json(name = "strategicAdvantage") val strategicAdvantage: String = "",
  @param:Json(name = "tailoredCoverSnippet") val tailoredCoverSnippet: String = "",
  @param:Json(name = "recommendedResumeVersion") val recommendedResumeVersion: String = "Resume_Adi_Strategy_v1.pdf",
  @param:Json(name = "applicationUrl") val applicationUrl: String = "",
  @param:Json(name = "crawledAt") val crawledAt: String = "Just now",
  val isSavedToApplicationDb: Boolean = false,
  val savedApplicationId: String? = null
)

@JsonClass(generateAdapter = true)
data class SmartJobSearchCrawlerResponse(
  @param:Json(name = "scanSummary") val scanSummary: String,
  @param:Json(name = "companiesCrawledCount") val companiesCrawledCount: Int,
  @param:Json(name = "rolesEvaluatedCount") val rolesEvaluatedCount: Int,
  @param:Json(name = "highFitMatchesCount") val highFitMatchesCount: Int,
  @param:Json(name = "matches") val matches: List<SmartJobMatch>
)
