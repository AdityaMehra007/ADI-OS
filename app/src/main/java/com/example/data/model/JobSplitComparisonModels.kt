package com.example.data.model

import com.example.ui.components.MatchTier

/**
 * Data structures for Split-View side-by-side comparison of two job descriptions
 * and their match scores against the candidate profile.
 */
data class JobSplitComparisonReport(
  val jobA: JobActionPlan,
  val matchA: ResumeJdMatchResult?,
  val jobB: JobActionPlan,
  val matchB: ResumeJdMatchResult?,
  val dimensions: List<ComparisonDimensionBreakdown> = emptyList(),
  val strategicTakeaway: String = "",
  val recommendedPrimaryTarget: String = ""
) {
  val scoreA: Int
    get() = matchA?.overallMatchScore ?: jobA.matchFitScore

  val scoreB: Int
    get() = matchB?.overallMatchScore ?: jobB.matchFitScore

  val scoreDelta: Int
    get() = scoreA - scoreB

  val tierA: MatchTier
    get() = matchA?.matchTier ?: MatchTier.fromScore(scoreA)

  val tierB: MatchTier
    get() = matchB?.matchTier ?: MatchTier.fromScore(scoreB)

  val leadingCompany: String
    get() = when {
      scoreA > scoreB -> jobA.targetCompany
      scoreB > scoreA -> jobB.targetCompany
      else -> "Tied"
    }

  val winningMarginSummary: String
    get() = when {
      scoreA > scoreB -> "+${scoreA - scoreB}% higher fit with ${jobA.targetCompany}"
      scoreB > scoreA -> "+${scoreB - scoreA}% higher fit with ${jobB.targetCompany}"
      else -> "Evenly matched (0% delta)"
    }
}

data class ComparisonDimensionBreakdown(
  val dimensionTitle: String,
  val categoryKey: String, // e.g. "OVERALL", "SKILLS", "EXPERIENCE", "METRICS", "ATS"
  val scoreA: Int,
  val scoreB: Int,
  val metricLabelA: String,
  val metricLabelB: String,
  val insightText: String
)

data class QuickComparisonPresetPair(
  val id: String,
  val label: String,
  val companyA: String,
  val companyB: String,
  val tag: String
)
