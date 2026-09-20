package com.example.data.model

/**
 * Data models for the Skills Radar feature.
 * Compares stored resume content against saved target job descriptions
 * and identifies missing keywords and certifications for targeted improvement.
 */

enum class MissingItemType {
  KEYWORD,
  CERTIFICATION
}

enum class GapPriority(val label: String, val colorHex: Long) {
  CRITICAL_ATS("Critical ATS Filter", 0xFFEF4444), // Crimson
  HIGH_ADVANTAGE("High Advantage", 0xFFF59E0B),    // Amber
  RECOMMENDED("Recommended Growth", 0xFF00E5FF)    // Cyan
}

data class MissingItemInsight(
  val id: String,
  val title: String,
  val type: MissingItemType,
  val priority: GapPriority,
  val category: String, // "Data & Analytics", "Ops Strategy", "Cloud & Infra", "Governance"
  val targetCompany: String,
  val targetRole: String,
  val presenceInTargetJd: String, // where it appears in the JD
  val candidateCurrentState: String, // what the stored resume currently has instead
  val suggestedAction: String, // actionable phrase or certification program
  val readyToInjectBullet: String, // pre-formatted resume bullet incorporating this keyword
  val isAddedToDraft: Boolean = false
)

data class SkillsRadarDimension(
  val id: String,
  val name: String,
  val shortLabel: String,
  val resumeScore: Float, // 0.0f - 1.0f (or 0 - 100)
  val targetJdScore: Float, // 0.0f - 1.0f (or 0 - 100)
  val matchedKeywords: List<String>,
  val missingKeywords: List<String>,
  val matchedCertifications: List<String>,
  val missingCertifications: List<String>,
  val tacticalAdvice: String
) {
  val gapDelta: Float get() = resumeScore - targetJdScore
  val isDeficit: Boolean get() = resumeScore < targetJdScore
}

data class TargetJobRequirementProfile(
  val id: String,
  val companyId: String,
  val companyName: String,
  val roleTitle: String,
  val department: String,
  val experienceTier: String,
  val compensationRange: String,
  val fullDescription: String,
  val requiredKeywords: List<String>,
  val preferredKeywords: List<String>,
  val requiredCertifications: List<String>,
  val preferredCertifications: List<String>,
  val dimensionTargets: Map<String, Float> // Dimension ID to required score (0.0 to 1.0)
)

data class SkillsRadarReport(
  val selectedResumeId: String,
  val selectedResumeTitle: String,
  val selectedJobId: String,
  val selectedJobTitle: String,
  val targetCompanyName: String,
  val overallMatchScore: Int, // 0 - 100
  val keywordMatchPercent: Int, // 0 - 100
  val certificationMatchPercent: Int, // 0 - 100
  val matchedKeywordsCount: Int,
  val totalRequiredKeywordsCount: Int,
  val matchedCertificationsCount: Int,
  val totalRequiredCertificationsCount: Int,
  val dimensions: List<SkillsRadarDimension>,
  val missingKeywords: List<MissingItemInsight>,
  val missingCertifications: List<MissingItemInsight>,
  val verifiedStrengths: List<String>,
  val executiveSummary: String,
  val lastComparedAt: String = "Live Radar • Stored Resume vs Target JD"
)
