package com.example.data.model

/**
 * Severity / Priority classification for skill gaps identified across target companies
 */
enum class SkillGapSeverity(val displayName: String, val badgeColorHex: String) {
  CRITICAL("Critical Requirement", "#EF4444"),
  HIGH_PRIORITY("High Priority", "#F59E0B"),
  COMPETITIVE_MOAT("Strategic Moat", "#10B981")
}

/**
 * Progression status of closing a specific skill gap
 */
enum class SkillGapStatus(val label: String) {
  IDENTIFIED("Identified"),
  IN_PROGRESS("Closing in Progress"),
  CLOSED("Closed & Verified")
}

/**
 * Represents a concrete skill gap diagnosed by analyzing saved companies and active job applications
 */
data class SkillGapItem(
  val id: String,
  val skillName: String,
  val category: String, // e.g. "System Architecture", "Executive Strategy", "Enterprise SQL", "Autonomous AI", "Product Analytics"
  val severity: SkillGapSeverity,
  val candidateLevel: String, // e.g. "Intermediate (Self-taught)", "Foundational", "Needs Production Proof"
  val requiredLevel: String, // e.g. "Production Grade", "Executive Standard", "Advanced High-Scale"
  val demandedByCompanies: List<String>, // e.g. ["Google", "Uber", "Swiggy"]
  val affectedApplicationsCount: Int,
  val relevanceRationale: String,
  val recommendedProjects: List<String>,
  val status: SkillGapStatus = SkillGapStatus.IDENTIFIED,
  val progress: Int = 0 // 0..100
)

/**
 * Specific milestone within a career growth phase
 */
data class CareerGrowthMilestone(
  val id: String,
  val title: String,
  val timeEstimate: String, // e.g. "Weeks 1-3", "Month 2"
  val focusArea: String,
  val keyDeliverable: String,
  val verificationCriteria: String,
  val proofOfWorkArtifact: String,
  val unlocksRoles: List<String> = emptyList(),
  val isCompleted: Boolean = false,
  val progress: Int = 0 // 0..100
)

/**
 * Sequential growth phase in the milestone-based career roadmap
 */
data class MilestoneGrowthPhase(
  val phaseId: String,
  val phaseNumber: Int,
  val title: String,
  val timeframe: String,
  val targetRoleTier: String,
  val projectedCompensation: String,
  val strategicFocus: String,
  val targetCompaniesUnlocked: List<String>,
  val skillGapsTargeted: List<String>,
  val milestones: List<CareerGrowthMilestone>
) {
  val completionPercentage: Int
    get() {
      if (milestones.isEmpty()) return 0
      val totalProgress = milestones.sumOf { it.progress }
      return (totalProgress / milestones.size).coerceIn(0, 100)
    }

  val isFullyCompleted: Boolean
    get() = milestones.isNotEmpty() && milestones.all { it.isCompleted || it.progress >= 100 }
}

/**
 * High-level diagnosis synthesized from saved companies and pipeline applications
 */
data class PipelineAnalysisMetrics(
  val savedCompaniesCount: Int,
  val activeApplicationsCount: Int,
  val interviewStageCount: Int,
  val topRequiredSkillsAcrossCompanies: List<String>,
  val mostDemandedCategory: String,
  val executiveMarketDiagnosis: String,
  val strategicAdvantageIdentified: String
)

/**
 * Full Career Roadmap report generated using Gemini API analyzing saved companies and current applications
 */
data class ComprehensiveCareerRoadmapReport(
  val id: String,
  val targetRole: String,
  val horizonYears: Int,
  val generatedAt: String,
  val isLiveGeminiGrounded: Boolean,
  val metrics: PipelineAnalysisMetrics,
  val skillGaps: List<SkillGapItem>,
  val growthPhases: List<MilestoneGrowthPhase>,
  val highLeverageTactics: List<String>,
  val geminiExecutiveVerdict: String
) {
  val totalMilestonesCount: Int
    get() = growthPhases.sumOf { it.milestones.size }

  val completedMilestonesCount: Int
    get() = growthPhases.sumOf { it.milestones.count { m -> m.isCompleted || m.progress >= 100 } }

  val overallProgressPercentage: Int
    get() {
      val total = totalMilestonesCount
      if (total == 0) return 0
      val totalProgress = growthPhases.sumOf { it.milestones.sumOf { m -> m.progress } }
      return (totalProgress / total).coerceIn(0, 100)
    }

  val closedSkillGapsCount: Int
    get() = skillGaps.count { it.status == SkillGapStatus.CLOSED || it.progress >= 100 }
}
