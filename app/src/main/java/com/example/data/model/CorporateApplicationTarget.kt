package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room SQLite entity to locally persist high-priority Corporate Application Targets,
 * compensation goals, pipeline statuses, and strategic angles across sessions.
 */
@Entity(
  tableName = "corporate_application_targets",
  indices = [
    Index(value = ["priorityTier"]),
    Index(value = ["applicationStatus"]),
    Index(value = ["isStarred"])
  ]
)
data class CorporateApplicationTarget(
  @PrimaryKey val id: String,
  val companyName: String,
  val targetRoleTitle: String,
  val priorityTier: String = "TIER_1_APEX", // TIER_1_APEX, TIER_2_STRATEGIC, TIER_3_PIPELINE
  val targetCtcLakhs: Double = 24.0,
  val applicationStatus: String = "TARGETED", // TARGETED, WAR_ROOM_READY, APPLIED, ROUND_1_CLEARED, OFFER_EXTENDED
  val hiringManager: String = "",
  val referralLead: String = "",
  val executiveHook: String = "",
  val alignmentScore: Int = 95,
  val targetQuarter: String = "Q3-Q4 2026",
  val lastActivityTimestamp: Long = System.currentTimeMillis(),
  val isStarred: Boolean = true
)
