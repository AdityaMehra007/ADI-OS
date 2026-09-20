package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.TargetCompany
import com.example.data.model.TargetCompanyMarketIntel
import com.example.data.model.TargetCompanyWithMarketIntelligence
import kotlinx.coroutines.flow.Flow

@Dao
interface TargetCompanyDao {

  @Transaction
  @Query("""
    SELECT * FROM target_companies 
    ORDER BY 
      CASE priorityTier 
        WHEN 'TIER_1_DREAM' THEN 1 
        WHEN 'TIER_2_STRATEGIC' THEN 2 
        ELSE 3 
      END,
      updatedAtTimestamp DESC
  """)
  fun getAllTargetCompaniesWithIntel(): Flow<List<TargetCompanyWithMarketIntelligence>>

  @Query("SELECT * FROM target_companies ORDER BY name ASC")
  fun getAllTargetCompanies(): Flow<List<TargetCompany>>

  @Transaction
  @Query("SELECT * FROM target_companies WHERE isWatched = 1 ORDER BY updatedAtTimestamp DESC")
  fun getWatchedTargetCompanies(): Flow<List<TargetCompanyWithMarketIntelligence>>

  @Transaction
  @Query("SELECT * FROM target_companies WHERE priorityTier = :priorityTier ORDER BY updatedAtTimestamp DESC")
  fun getTargetCompaniesByPriority(priorityTier: String): Flow<List<TargetCompanyWithMarketIntelligence>>

  @Transaction
  @Query("SELECT * FROM target_companies WHERE trackingStatus = :status ORDER BY updatedAtTimestamp DESC")
  fun getTargetCompaniesByStatus(status: String): Flow<List<TargetCompanyWithMarketIntelligence>>

  @Transaction
  @Query("SELECT * FROM target_companies WHERE id = :id LIMIT 1")
  fun getTargetCompanyWithIntelFlow(id: String): Flow<TargetCompanyWithMarketIntelligence?>

  @Transaction
  @Query("SELECT * FROM target_companies WHERE id = :id LIMIT 1")
  suspend fun getTargetCompanyWithIntelById(id: String): TargetCompanyWithMarketIntelligence?

  @Query("SELECT * FROM target_companies WHERE id = :id LIMIT 1")
  suspend fun getTargetCompanyById(id: String): TargetCompany?

  @Query("SELECT * FROM company_market_intelligence WHERE targetCompanyId = :companyId ORDER BY lastRefreshedTimestamp DESC")
  fun getMarketIntelForCompany(companyId: String): Flow<List<TargetCompanyMarketIntel>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTargetCompany(company: TargetCompany)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTargetCompanies(companies: List<TargetCompany>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMarketIntel(intel: TargetCompanyMarketIntel)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMarketIntelList(intelList: List<TargetCompanyMarketIntel>)

  @Update
  suspend fun updateTargetCompany(company: TargetCompany)

  @Query("UPDATE target_companies SET trackingStatus = :status, updatedAtTimestamp = :timestamp WHERE id = :id")
  suspend fun updateTrackingStatus(id: String, status: String, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE target_companies SET priorityTier = :tier, updatedAtTimestamp = :timestamp WHERE id = :id")
  suspend fun updatePriorityTier(id: String, tier: String, timestamp: Long = System.currentTimeMillis())

  @Query("UPDATE target_companies SET isWatched = :isWatched WHERE id = :id")
  suspend fun toggleWatch(id: String, isWatched: Boolean)

  @Query("DELETE FROM target_companies WHERE id = :id")
  suspend fun deleteTargetCompany(id: String)

  @Query("DELETE FROM company_market_intelligence WHERE targetCompanyId = :companyId")
  suspend fun deleteMarketIntelForCompany(companyId: String)

  @Query("DELETE FROM company_market_intelligence WHERE id = :id")
  suspend fun deleteMarketIntelById(id: String)

  @Query("SELECT COUNT(*) FROM target_companies")
  suspend fun getTargetCompaniesCount(): Int
}
