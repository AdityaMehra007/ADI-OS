package com.example.data.model

/**
 * Data models representing a synthesized Weekly Career Health Report.
 * Combines telemetry from the Skills Radar (Resume vs Target JDs)
 * and the Career Velocity Dashboard (Pacing & Milestones) into an
 * executive-grade briefing suitable for PDF export and high-level review.
 */

data class HealthDimensionSummary(
  val id: String,
  val title: String,
  val resumeScorePct: Int,
  val targetJdScorePct: Int,
  val deltaPct: Int,
  val status: String // "STRONG_LEVERAGE", "OPTIMAL", "CRITICAL_GAP"
)

data class WeeklySprintObjective(
  val id: String,
  val priorityRank: Int,
  val category: String, // "RESUME_ATS_INJECTION", "CERTIFICATION_ACQUISITION", "EXECUTIVE_NETWORKING", "PORTFOLIO_PROOF"
  val title: String,
  val rationale: String,
  val estimatedImpact: String,
  val isCompleted: Boolean = false
)

data class WeeklyCareerHealthReport(
  val reportId: String,
  val weekTitle: String,
  val periodRange: String,
  val candidateName: String = "Aditya Mehra",
  val candidateRole: String = "Lead Strategy & Operations",
  val targetBenchmarkCompany: String,
  val targetBenchmarkRole: String,
  val resumeVersionAnalyzed: String,

  // Composite Performance Indicators
  val compositeHealthScore: Int, // 0 - 100
  val healthGrade: String, // "TIER-1 ELITE", "STRONG ADVANTAGE", "SURGING READINESS"
  val pacingStatus: String, // "ACCELERATING • 1.4x Market Benchmark"
  val marketReadinessPct: Int,
  val daysToFullMarketReadiness: Int,

  // Career Velocity Synthesis
  val velocityIndex: Int,
  val monthlySkillGainRate: Float,
  val verifiedRequirementsCount: Int,
  val totalRequirementsCount: Int,
  val velocityFocusArea: String,
  val compensationTrajectory: String,

  // Skills Radar Synthesis
  val skillsRadarMatchScore: Int,
  val keywordMatchPct: Int,
  val certMatchPct: Int,
  val dimensionScores: List<HealthDimensionSummary>,
  val criticalMissingKeywords: List<String>,
  val missingCertifications: List<String>,
  val verifiedMoatStrengths: List<String>,

  // Executive Narrative & Actionable Directives
  val executiveSummaryNarrative: String,
  val strategicStrengthsNarrative: String,
  val vulnerabilityAnalysis: String,
  val weeklySprintObjectives: List<WeeklySprintObjective>,
  val generatedTimestamp: String = "Live Engine • Synchronized Telemetry"
)
