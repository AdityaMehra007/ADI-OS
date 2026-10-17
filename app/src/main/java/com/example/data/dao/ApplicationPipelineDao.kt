package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ApplicationPipelineCompanyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Application Pipeline company cards.
 * Enables reactive offline queries, status tracking, and offline mutations.
 */
@Dao
interface ApplicationPipelineDao {

  @Query("SELECT * FROM application_pipeline_companies ORDER BY isStarred DESC, companyName ASC")
  fun getAllPipelineCompanies(): Flow<List<ApplicationPipelineCompanyEntity>>

  @Query("SELECT * FROM application_pipeline_companies WHERE status = :status ORDER BY isStarred DESC, companyName ASC")
  fun getCompaniesByStatus(status: String): Flow<List<ApplicationPipelineCompanyEntity>>

  @Query("SELECT * FROM application_pipeline_companies WHERE companyName = :companyName LIMIT 1")
  suspend fun getCompanyByName(companyName: String): ApplicationPipelineCompanyEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCompany(company: ApplicationPipelineCompanyEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(companies: List<ApplicationPipelineCompanyEntity>)

  @Update
  suspend fun updateCompany(company: ApplicationPipelineCompanyEntity)

  @Query("UPDATE application_pipeline_companies SET status = :newStatus, lastUpdated = :lastUpdated, offlineCachedTimestamp = :timestamp WHERE companyName = :companyName")
  suspend fun updateStatus(
    companyName: String,
    newStatus: String,
    lastUpdated: String,
    timestamp: Long = System.currentTimeMillis()
  )

  @Query("UPDATE application_pipeline_companies SET isStarred = :isStarred WHERE companyName = :companyName")
  suspend fun toggleStarred(companyName: String, isStarred: Boolean)

  @Query("DELETE FROM application_pipeline_companies WHERE companyName = :companyName")
  suspend fun deleteCompany(companyName: String)

  @Query("SELECT COUNT(*) FROM application_pipeline_companies")
  suspend fun getCount(): Int

  @Query("SELECT COUNT(*) FROM application_pipeline_companies WHERE status = :status")
  suspend fun getCountByStatus(status: String): Int
}
