package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ResumeVariation
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object for Resume Variations and Version History.
 */
@Dao
interface ResumeVariationDao {

  @Query("SELECT * FROM resume_variations ORDER BY isPrimary DESC, updatedAt DESC")
  fun getAllVariationsFlow(): Flow<List<ResumeVariation>>

  @Query("SELECT * FROM resume_variations WHERE targetCompany = :company ORDER BY versionNumber DESC")
  fun getVariationsByCompanyFlow(company: String): Flow<List<ResumeVariation>>

  @Query("SELECT * FROM resume_variations WHERE id = :id LIMIT 1")
  fun getVariationByIdFlow(id: String): Flow<ResumeVariation?>

  @Query("SELECT * FROM resume_variations WHERE isPrimary = 1 LIMIT 1")
  fun getPrimaryVariationFlow(): Flow<ResumeVariation?>

  @Query("SELECT * FROM resume_variations ORDER BY isPrimary DESC, updatedAt DESC")
  suspend fun getAllVariations(): List<ResumeVariation>

  @Query("SELECT * FROM resume_variations WHERE id = :id LIMIT 1")
  suspend fun getVariationById(id: String): ResumeVariation?

  @Query("SELECT * FROM resume_variations WHERE isPrimary = 1 LIMIT 1")
  suspend fun getPrimaryVariation(): ResumeVariation?

  @Query("SELECT * FROM resume_variations WHERE targetCompany = :company ORDER BY versionNumber DESC")
  suspend fun getVariationsByCompany(company: String): List<ResumeVariation>

  @Query("SELECT * FROM resume_variations WHERE parentVariationId = :parentId OR id = :parentId ORDER BY versionNumber ASC")
  suspend fun getVersionHistoryFamily(parentId: String): List<ResumeVariation>

  @Query("SELECT MAX(versionNumber) FROM resume_variations WHERE targetCompany = :company AND targetRole = :role")
  suspend fun getMaxVersionForRole(company: String, role: String): Int?

  @Query("SELECT COUNT(*) FROM resume_variations")
  suspend fun getVariationCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(variation: ResumeVariation)

  suspend fun insertVariation(variation: ResumeVariation) {
    insertOrUpdate(variation)
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertVariations(variations: List<ResumeVariation>)

  fun getVariationsForCompany(company: String): Flow<List<ResumeVariation>> = getVariationsByCompanyFlow(company)

  @Query("SELECT * FROM resume_variations WHERE parentVariationId = :parentId OR id = :parentId ORDER BY versionNumber DESC")
  suspend fun getVersionHistoryForLineage(parentId: String): List<ResumeVariation>

  @Query("UPDATE resume_variations SET overallMatchScore = :overallMatchScore, atsCompatibilityScore = :atsCompatibilityScore, updatedAt = :updatedAt WHERE id = :id")
  suspend fun updateScores(id: String, overallMatchScore: Int, atsCompatibilityScore: Int, updatedAt: Long = System.currentTimeMillis())

  @Update
  suspend fun update(variation: ResumeVariation)

  @Delete
  suspend fun delete(variation: ResumeVariation)

  @Query("DELETE FROM resume_variations WHERE id = :id")
  suspend fun deleteById(id: String)

  @Query("UPDATE resume_variations SET isPrimary = 0")
  suspend fun clearPrimaryStatus()

  @Query("UPDATE resume_variations SET isPrimary = 1, updatedAt = :updatedAt WHERE id = :id")
  suspend fun markAsPrimary(id: String, updatedAt: Long = System.currentTimeMillis())

  @Transaction
  suspend fun setPrimaryVariation(id: String) {
    clearPrimaryStatus()
    markAsPrimary(id)
  }

  @Query("UPDATE resume_variations SET resumeText = :newText, wordCount = :wordCount, updatedAt = :updatedAt WHERE id = :id")
  suspend fun updateResumeContent(id: String, newText: String, wordCount: Int, updatedAt: Long = System.currentTimeMillis())
}
