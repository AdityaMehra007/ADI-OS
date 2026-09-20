package com.example.service

import java.util.UUID

data class JobDiscoveryLogItem(
  val id: String = UUID.randomUUID().toString(),
  val timestamp: String,
  val triggerSource: String, // BACKGROUND_RADAR, MANUAL_SWARM, WATCHLIST_EVENT, GOAL_REALIGNMENT
  val targetCompaniesScannedCount: Int,
  val companiesScannedNames: List<String> = emptyList(),
  val jobsDiscoveredCount: Int,
  val newJobsStoredCount: Int,
  val highMatchCount: Int,
  val topJobTitles: List<String> = emptyList(),
  val summaryMessage: String,
  val activeGoalAlignment: String,
  val status: String = "SUCCESS"
)

data class JobPullResult(
  val success: Boolean,
  val companiesScannedCount: Int,
  val jobsPulledCount: Int,
  val newJobsStoredCount: Int,
  val highMatchJobsCount: Int,
  val storedJobTitles: List<String>,
  val message: String
)
