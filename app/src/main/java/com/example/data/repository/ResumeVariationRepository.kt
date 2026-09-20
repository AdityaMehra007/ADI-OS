package com.example.data.repository

import com.example.data.dao.ResumeVariationDao
import com.example.data.model.ResumeVariation
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository pattern implementation for managing resume variations and versioning.
 * Provides clean abstractions over Room DAO operations for ViewModels.
 */
class ResumeVariationRepository(
  private val resumeVariationDao: ResumeVariationDao
) {

  val allVariationsFlow: Flow<List<ResumeVariation>> =
    resumeVariationDao.getAllVariationsFlow()

  val primaryVariationFlow: Flow<ResumeVariation?> =
    resumeVariationDao.getPrimaryVariationFlow()

  fun getVariationsByCompanyFlow(company: String): Flow<List<ResumeVariation>> =
    resumeVariationDao.getVariationsByCompanyFlow(company)

  fun getVariationByIdFlow(id: String): Flow<ResumeVariation?> =
    resumeVariationDao.getVariationByIdFlow(id)

  suspend fun getAllVariations(): List<ResumeVariation> =
    resumeVariationDao.getAllVariations()

  suspend fun getVariationById(id: String): ResumeVariation? =
    resumeVariationDao.getVariationById(id)

  suspend fun getPrimaryVariation(): ResumeVariation? =
    resumeVariationDao.getPrimaryVariation()

  suspend fun insertOrUpdate(variation: ResumeVariation) {
    resumeVariationDao.insertOrUpdate(variation)
  }

  suspend fun setPrimary(id: String) {
    resumeVariationDao.setPrimaryVariation(id)
  }

  suspend fun deleteVariation(id: String) {
    resumeVariationDao.deleteById(id)
  }

  suspend fun updateContent(id: String, newText: String) {
    val wordCount = calculateWordCount(newText)
    resumeVariationDao.updateResumeContent(id, newText, wordCount)
  }

  suspend fun updateScores(id: String, overallMatchScore: Int, atsCompatibilityScore: Int) {
    resumeVariationDao.updateScores(id, overallMatchScore, atsCompatibilityScore)
  }

  suspend fun getVersionHistoryForLineage(parentId: String): List<ResumeVariation> {
    return resumeVariationDao.getVersionHistoryForLineage(parentId)
  }

  /**
   * Creates a new version (v+1) branching from an existing variation.
   */
  suspend fun createNewVersion(
    parentVariation: ResumeVariation,
    updatedResumeText: String,
    versionNotes: String,
    atsScore: Int = parentVariation.atsCompatibilityScore,
    matchScore: Int = parentVariation.overallMatchScore
  ): ResumeVariation {
    val nextVersion = parentVariation.versionNumber + 1
    val versionLabel = "v$nextVersion.0"
    val wordCount = calculateWordCount(updatedResumeText)

    val newVersion = ResumeVariation(
      id = UUID.randomUUID().toString(),
      title = "${parentVariation.title} ($versionLabel)",
      targetCompany = parentVariation.targetCompany,
      targetRole = parentVariation.targetRole,
      resumeText = updatedResumeText,
      versionNumber = nextVersion,
      versionLabel = versionLabel,
      versionNotes = versionNotes.ifBlank { "Version update based on role criteria" },
      isPrimary = false,
      parentVariationId = parentVariation.id,
      atsCompatibilityScore = atsScore,
      overallMatchScore = matchScore,
      tags = parentVariation.tags,
      wordCount = wordCount,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis()
    )

    resumeVariationDao.insertOrUpdate(newVersion)
    return newVersion
  }

  /**
   * Creates a fresh new variation tailored for a specific company or role.
   */
  suspend fun createVariation(
    title: String,
    targetCompany: String,
    targetRole: String,
    resumeText: String,
    versionNotes: String = "Initial baseline version",
    tags: String = "Operations, Analytics",
    isPrimary: Boolean = false,
    parentVariationId: String? = null
  ): ResumeVariation {
    val wordCount = calculateWordCount(resumeText)
    val variation = ResumeVariation(
      id = UUID.randomUUID().toString(),
      title = title,
      targetCompany = targetCompany,
      targetRole = targetRole,
      resumeText = resumeText,
      versionNumber = 1,
      versionLabel = "v1.0",
      versionNotes = versionNotes,
      isPrimary = isPrimary,
      parentVariationId = parentVariationId,
      atsCompatibilityScore = 85,
      overallMatchScore = 88,
      tags = tags,
      wordCount = wordCount,
      createdAt = System.currentTimeMillis(),
      updatedAt = System.currentTimeMillis()
    )

    if (isPrimary) {
      resumeVariationDao.clearPrimaryStatus()
    }
    resumeVariationDao.insertOrUpdate(variation)
    return variation
  }

  private fun calculateWordCount(text: String): Int {
    if (text.isBlank()) return 0
    return text.trim().split(Regex("\\s+")).size
  }
}
