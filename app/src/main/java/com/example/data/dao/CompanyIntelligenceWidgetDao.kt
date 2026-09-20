package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.CompanyIntelligenceWidgetCache
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object (DAO) for managing local persistence of Company Intelligence
 * widget state across app restarts.
 */
@Dao
interface CompanyIntelligenceWidgetDao {

  @Query("SELECT * FROM company_intelligence_widget_cache WHERE isLastSelected = 1 ORDER BY lastUpdatedMillis DESC LIMIT 1")
  fun getLastSelectedWidgetCacheFlow(): Flow<CompanyIntelligenceWidgetCache?>

  @Query("SELECT * FROM company_intelligence_widget_cache WHERE isLastSelected = 1 ORDER BY lastUpdatedMillis DESC LIMIT 1")
  suspend fun getLastSelectedWidgetCache(): CompanyIntelligenceWidgetCache?

  @Query("SELECT * FROM company_intelligence_widget_cache WHERE LOWER(companyName) = LOWER(:companyName) LIMIT 1")
  fun getCacheForCompanyFlow(companyName: String): Flow<CompanyIntelligenceWidgetCache?>

  @Query("SELECT * FROM company_intelligence_widget_cache WHERE LOWER(companyName) = LOWER(:companyName) LIMIT 1")
  suspend fun getCacheForCompany(companyName: String): CompanyIntelligenceWidgetCache?

  @Query("SELECT * FROM company_intelligence_widget_cache ORDER BY lastUpdatedMillis DESC")
  fun getAllCachedWidgetSummariesFlow(): Flow<List<CompanyIntelligenceWidgetCache>>

  @Query("SELECT * FROM company_intelligence_widget_cache ORDER BY lastUpdatedMillis DESC")
  suspend fun getAllCachedWidgetSummaries(): List<CompanyIntelligenceWidgetCache>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(cache: CompanyIntelligenceWidgetCache)

  @Query("UPDATE company_intelligence_widget_cache SET isLastSelected = 0")
  suspend fun clearLastSelected()

  @Transaction
  suspend fun saveLastSelectedWidgetCache(cache: CompanyIntelligenceWidgetCache) {
    clearLastSelected()
    insertOrUpdate(cache.copy(isLastSelected = true, lastUpdatedMillis = System.currentTimeMillis()))
  }

  @Transaction
  suspend fun markCompanyAsLastSelected(companyName: String) {
    clearLastSelected()
    val existing = getCacheForCompany(companyName)
    if (existing != null) {
      insertOrUpdate(existing.copy(isLastSelected = true, lastUpdatedMillis = System.currentTimeMillis()))
    } else {
      insertOrUpdate(
        CompanyIntelligenceWidgetCache(
          companyName = companyName,
          cachedSummary = "",
          isLastSelected = true,
          lastUpdatedMillis = System.currentTimeMillis()
        )
      )
    }
  }

  @Query("DELETE FROM company_intelligence_widget_cache WHERE LOWER(companyName) = LOWER(:companyName)")
  suspend fun deleteCacheByCompany(companyName: String)

  @Query("DELETE FROM company_intelligence_widget_cache")
  suspend fun clearAll()
}
