package com.example.data.model

/**
 * Represents a local mock document available for ingestion and parsing.
 */
data class MockResumeDocument(
  val id: String,
  val fileName: String,
  val fileType: String, // "PDF", "DOCX", "TXT", "MARKDOWN"
  val simulatedSizeBytes: Long,
  val title: String,
  val targetProfile: String,
  val description: String,
  val rawContent: String
)

/**
 * Parsed contact and header information from the document.
 */
data class CandidateContactInfo(
  val candidateName: String,
  val email: String,
  val phone: String,
  val location: String,
  val linkedInUrl: String = "",
  val gitHubUrl: String = "",
  val portfolioUrl: String = ""
)

/**
 * Individual parsed experience entry.
 */
data class ParsedExperienceItem(
  val company: String,
  val roleTitle: String,
  val dateRange: String,
  val location: String = "Bengaluru, India",
  val bullets: List<String> = emptyList(),
  val quantifiedBulletsCount: Int = 0
)

/**
 * Individual parsed education entry.
 */
data class ParsedEducationItem(
  val institution: String,
  val degree: String,
  val graduationYear: String,
  val gpaOrHonors: String = ""
)

/**
 * Comprehensive representation of a locally parsed resume document.
 */
data class ParsedResumeDocument(
  val id: String,
  val fileName: String,
  val fileType: String,
  val fileSizeBytes: Long,
  val wordCount: Int,
  val charCount: Int,
  val parseTimestamp: String,
  val contactInfo: CandidateContactInfo,
  val parsedSummary: String,
  val experiences: List<ParsedExperienceItem>,
  val education: List<ParsedEducationItem>,
  val skills: List<String>,
  val certifications: List<String>,
  val proofOfWorkProjects: List<String>,
  val rawText: String,
  // Document Health Heuristics
  val quantifiableMetricRatio: Float, // % of bullets containing numbers/metrics
  val actionVerbStrengthScore: Int, // 0-100
  val atsFormattingScore: Int, // 0-100
  val wordDensityRating: String, // "OPTIMAL (450-650 words)", "CONCISE", "WORDY"
  val detectedAtsKeywords: List<String>,
  val weakVerbOccurrences: List<String> = emptyList(),
  val strongPowerVerbsDetected: List<String> = emptyList()
)

/**
 * Constructive critique on a specific resume section.
 */
data class SectionOptimizationCritique(
  val sectionName: String,
  val score: Int, // 0-100
  val status: String, // "OPTIMIZED", "NEEDS_ATTENTION", "CRITICAL_GAP"
  val critique: String,
  val actionableGuidance: String
)

/**
 * Side-by-side bullet transformation from passive/weak to quantified power bullet.
 */
data class BulletOptimizationPair(
  val id: String,
  val roleOrSection: String,
  val originalBullet: String,
  val improvedBullet: String,
  val metricAdded: String,
  val atsKeywordsInjected: List<String>,
  val reasoning: String,
  val isApplied: Boolean = false
)

/**
 * Keyword audit against target role expectations.
 */
data class AtsKeywordAudit(
  val matchedKeywords: List<String>,
  val missingHighPriorityKeywords: List<String>,
  val overusedClichePhrases: List<String>
)

/**
 * Full constructive feedback generated via Gemini API for resume optimization.
 */
data class ResumeOptimizationFeedback(
  val id: String,
  val overallScore: Int, // 0-100
  val atsPassRatePercent: Int, // 0-100
  val executiveVerdict: String,
  val targetRoleEvaluated: String,
  val targetCompanyEvaluated: String,
  val marketAlignmentTier: String, // e.g. "Tier-1 Quick-Commerce Caliber (Top 3%)"
  val modelUsed: String = "gemini-3.5-flash",
  val timestamp: String,
  val isLiveApi: Boolean,
  val sectionCritiques: List<SectionOptimizationCritique>,
  val bulletTransformations: List<BulletOptimizationPair>,
  val keywordAudit: AtsKeywordAudit,
  val topImmediatePriorities: List<String>,
  val formattingAndLengthGuidance: String,
  val rawGeminiOutput: String = ""
)
