package com.example.data.model

data class GroundedSource(
  val title: String,
  val url: String,
  val snippet: String = ""
)

data class CompanyIntelReport(
  val companyName: String,
  val timestamp: String,
  val businessNewsSummary: String,
  val hiringUpdates: List<String>,
  val executiveShifts: List<String>,
  val strategicRisksAndOpportunities: String,
  val interviewAngles: List<String>,
  val searchQueriesUsed: List<String>,
  val groundedSources: List<GroundedSource>,
  val isLiveGrounded: Boolean = true,
  val confidenceScore: Int = 96
)
