package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DarkStoreSimulationSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface DarkStoreSimulationDao {

  @Query("SELECT * FROM dark_store_simulation_settings WHERE id = :id LIMIT 1")
  fun getSimulationSettings(id: String = "primary_settings"): Flow<DarkStoreSimulationSettings?>

  @Query("SELECT * FROM dark_store_simulation_settings WHERE id = :id LIMIT 1")
  suspend fun getSimulationSettingsSync(id: String = "primary_settings"): DarkStoreSimulationSettings?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveSimulationSettings(settings: DarkStoreSimulationSettings)

  @Update
  suspend fun updateSimulationSettings(settings: DarkStoreSimulationSettings)

  @Query("UPDATE dark_store_simulation_settings SET selectedStressMode = :stressMode, lastUpdatedTimestamp = :timestamp WHERE id = :id")
  suspend fun updateStressMode(id: String = "primary_settings", stressMode: String, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE dark_store_simulation_settings SET selectedHubId = :hubId, lastUpdatedTimestamp = :timestamp WHERE id = :id")
  suspend fun updateSelectedHub(id: String = "primary_settings", hubId: String, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE dark_store_simulation_settings SET totalSimulatedCycles = totalSimulatedCycles + 1, lastUpdatedTimestamp = :timestamp WHERE id = :id")
  suspend fun incrementSimulatedCycle(id: String = "primary_settings", timestamp: Long = System.currentTimeMillis())
}
