package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CorporateApplicationTarget
import kotlinx.coroutines.flow.Flow

@Dao
interface CorporateApplicationTargetDao {

  @Query("SELECT * FROM corporate_application_targets ORDER BY isStarred DESC, targetCtcLakhs DESC")
  fun getAllTargets(): Flow<List<CorporateApplicationTarget>>

  @Query("SELECT * FROM corporate_application_targets WHERE isStarred = 1 ORDER BY targetCtcLakhs DESC")
  fun getStarredTargets(): Flow<List<CorporateApplicationTarget>>

  @Query("SELECT * FROM corporate_application_targets WHERE id = :id LIMIT 1")
  suspend fun getTargetById(id: String): CorporateApplicationTarget?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTarget(target: CorporateApplicationTarget)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllTargets(targets: List<CorporateApplicationTarget>)

  @Update
  suspend fun updateTarget(target: CorporateApplicationTarget)

  @Query("DELETE FROM corporate_application_targets WHERE id = :id")
  suspend fun deleteTargetById(id: String)

  @Query("UPDATE corporate_application_targets SET applicationStatus = :newStatus, lastActivityTimestamp = :timestamp WHERE id = :id")
  suspend fun updateStatus(id: String, newStatus: String, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE corporate_application_targets SET isStarred = :isStarred WHERE id = :id")
  suspend fun toggleStarred(id: String, isStarred: Boolean)

  @Query("SELECT COUNT(*) FROM corporate_application_targets")
  suspend fun getCount(): Int
}
