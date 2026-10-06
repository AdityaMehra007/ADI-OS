package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room SQLite entity to locally persist Quick-Commerce Dark Store
 * simulation configurations, SLA targets, and active stress parameters.
 */
@Entity(tableName = "dark_store_simulation_settings")
data class DarkStoreSimulationSettings(
  @PrimaryKey val id: String = "primary_settings",
  val selectedHubId: String = "HSR_03",
  val selectedStressMode: String = "NORMAL",
  val autoRefreshIntervalSec: Int = 5,
  val targetDispatchSlaSec: Int = 110,
  val targetPickerSlaSec: Int = 40,
  val targetStagingSlaSec: Int = 105,
  val targetContributionMargin: Float = 34.50f,
  val targetFulfillmentPercentage: Float = 99.8f,
  val batchingRadiusKm: Float = 1.2f,
  val slaWatchdogEnabled: Boolean = true,
  val alertThresholdDeviationSec: Int = 15,
  val totalSimulatedCycles: Int = 0,
  val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
