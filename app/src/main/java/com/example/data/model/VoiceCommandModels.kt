package com.example.data.model

enum class VoiceIntentType {
  JOB_SEARCH,
  SUMMARIZE_COMPANY_NEWS,
  AUTOMATION_DISPATCH,
  CRITERIA_UPDATE,
  COPILOT_BRIEFING,
  GENERAL_COMMAND
}

data class VoiceCommandInterpretation(
  val rawSpokenText: String,
  val intentType: VoiceIntentType,
  val targetQuery: String = "",
  val targetCompany: String = "",
  val roleKeywords: List<String> = emptyList(),
  val location: String = "",
  val minSalaryLakhs: Int? = null,
  val isFresherOnly: Boolean? = null,
  val summaryHeadline: String = "",
  val summaryResult: String = "",
  val matchedJobsCount: Int = 0,
  val companyIntelReport: CompanyIntelReport? = null,
  val actionDescription: String = "",
  val confidence: Int = 98,
  val suggestedFollowUps: List<String> = emptyList()
)

data class VoiceCommandHistoryItem(
  val id: String,
  val timestamp: String,
  val spokenText: String,
  val interpretation: VoiceCommandInterpretation
)
