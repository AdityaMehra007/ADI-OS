package com.example.service

import com.example.data.model.CandidateMatchStatus
import com.example.data.model.ComparisonDimensionBreakdown
import com.example.data.model.JobActionPlan
import com.example.data.model.JobSplitComparisonReport
import com.example.data.model.QuickComparisonPresetPair
import com.example.data.model.ResumeJdMatchResult

object JobSplitComparisonService {

  val PRESET_COMPARISON_PAIRS = listOf(
    QuickComparisonPresetPair(
      id = "preset_zepto_blinkit",
      label = "Zepto vs Blinkit",
      companyA = "Zepto",
      companyB = "Blinkit",
      tag = "Quick Commerce"
    ),
    QuickComparisonPresetPair(
      id = "preset_zepto_razorpay",
      label = "Zepto vs Razorpay",
      companyA = "Zepto",
      companyB = "Razorpay",
      tag = "Ops vs Fintech"
    ),
    QuickComparisonPresetPair(
      id = "preset_razorpay_mckinsey",
      label = "Razorpay vs McKinsey",
      companyA = "Razorpay",
      companyB = "McKinsey & Company",
      tag = "Fintech vs Strategy"
    ),
    QuickComparisonPresetPair(
      id = "preset_zepto_swiggy",
      label = "Zepto vs Swiggy",
      companyA = "Zepto",
      companyB = "Swiggy",
      tag = "Dark Store vs City Scale"
    )
  )

  fun buildComparisonReport(
    jobA: JobActionPlan,
    matchA: ResumeJdMatchResult?,
    jobB: JobActionPlan,
    matchB: ResumeJdMatchResult?
  ): JobSplitComparisonReport {
    val scoreA = matchA?.overallMatchScore ?: jobA.matchFitScore
    val scoreB = matchB?.overallMatchScore ?: jobB.matchFitScore

    val skillsScoreA = matchA?.skillsMatchScore ?: computeFallbackSkillsScore(jobA)
    val skillsScoreB = matchB?.skillsMatchScore ?: computeFallbackSkillsScore(jobB)

    val expScoreA = matchA?.experienceFitScore ?: computeFallbackExpScore(jobA)
    val expScoreB = matchB?.experienceFitScore ?: computeFallbackExpScore(jobB)

    val metricsScoreA = matchA?.metricsScore ?: 88
    val metricsScoreB = matchB?.metricsScore ?: 82

    val atsScoreA = computeAtsScreeningScore(jobA, matchA)
    val atsScoreB = computeAtsScreeningScore(jobB, matchB)

    val dimensions = listOf(
      ComparisonDimensionBreakdown(
        dimensionTitle = "Overall Match Fit",
        categoryKey = "OVERALL",
        scoreA = scoreA,
        scoreB = scoreB,
        metricLabelA = "$scoreA% Fit",
        metricLabelB = "$scoreB% Fit",
        insightText = if (scoreA > scoreB) {
          "${jobA.targetCompany} provides a +${scoreA - scoreB}% stronger comprehensive candidate-role alignment."
        } else if (scoreB > scoreA) {
          "${jobB.targetCompany} leads overall fit by +${scoreB - scoreA}%, closely matching core competencies."
        } else {
          "Both opportunities demonstrate identical composite qualification alignment."
        }
      ),
      ComparisonDimensionBreakdown(
        dimensionTitle = "Core Skills & Toolkit",
        categoryKey = "SKILLS",
        scoreA = skillsScoreA,
        scoreB = skillsScoreB,
        metricLabelA = "$skillsScoreA% Match",
        metricLabelB = "$skillsScoreB% Match",
        insightText = "${jobA.targetCompany}: ${jobA.requiredSkills.count { it.candidateMatchStatus == CandidateMatchStatus.STRONG_MATCH }} strong matches vs ${jobB.targetCompany}: ${jobB.requiredSkills.count { it.candidateMatchStatus == CandidateMatchStatus.STRONG_MATCH }}."
      ),
      ComparisonDimensionBreakdown(
        dimensionTitle = "Domain & Experience Depth",
        categoryKey = "EXPERIENCE",
        scoreA = expScoreA,
        scoreB = expScoreB,
        metricLabelA = "$expScoreA% Fit",
        metricLabelB = "$expScoreB% Fit",
        insightText = if (expScoreA >= expScoreB) {
          "${jobA.targetCompany} role directly builds on verified dark store / operations velocity."
        } else {
          "${jobB.targetCompany} role commands additional depth in specialized domain protocols."
        }
      ),
      ComparisonDimensionBreakdown(
        dimensionTitle = "Quantifiable Metric Infusion",
        categoryKey = "METRICS",
        scoreA = metricsScoreA,
        scoreB = metricsScoreB,
        metricLabelA = "$metricsScoreA% Proof",
        metricLabelB = "$metricsScoreB% Proof",
        insightText = "Candidate resume contains measurable telemetry (cycle reduction, SLAs) aligned to ${if (metricsScoreA >= metricsScoreB) jobA.targetCompany else jobB.targetCompany}."
      ),
      ComparisonDimensionBreakdown(
        dimensionTitle = "ATS Screening Keyword Match",
        categoryKey = "ATS",
        scoreA = atsScoreA,
        scoreB = atsScoreB,
        metricLabelA = "$atsScoreA% Overlap",
        metricLabelB = "$atsScoreB% Overlap",
        insightText = "Initial resume algorithmic screening likelihood against automated ATS filters."
      )
    )

    val winner = if (scoreA >= scoreB) jobA else jobB
    val runnerUp = if (scoreA >= scoreB) jobB else jobA
    val delta = kotlin.math.abs(scoreA - scoreB)

    val strategicTakeaway = if (delta >= 5) {
      "${winner.targetCompany} emerges as the statistically superior high-probability target (${if (winner == jobA) scoreA else scoreB}% match vs ${if (winner == jobA) scoreB else scoreA}%). Your background in operational throughput and automated telemetry unlocks immediate traction. ${runnerUp.targetCompany} remains viable as an alternative target, but requires tailored resume framing around ${runnerUp.requiredSkills.firstOrNull { it.candidateMatchStatus != CandidateMatchStatus.STRONG_MATCH }?.name ?: "domain-specific frameworks"}."
    } else {
      "Both roles present highly competitive match scores (${scoreA}% at ${jobA.targetCompany} vs ${scoreB}% at ${jobB.targetCompany}). Your decision hinges primarily on company mission and team culture: ${jobA.targetCompany} rewards high-tempo execution and immediate operational ownership, while ${jobB.targetCompany} emphasizes structured multi-stakeholder strategy."
    }

    val primaryTarget = "${winner.targetCompany} (${winner.targetRole})"

    return JobSplitComparisonReport(
      jobA = jobA,
      matchA = matchA,
      jobB = jobB,
      matchB = matchB,
      dimensions = dimensions,
      strategicTakeaway = strategicTakeaway,
      recommendedPrimaryTarget = primaryTarget
    )
  }

  private fun computeFallbackSkillsScore(plan: JobActionPlan): Int {
    if (plan.requiredSkills.isEmpty()) return 85
    val strong = plan.requiredSkills.count { it.candidateMatchStatus == CandidateMatchStatus.STRONG_MATCH }
    val partial = plan.requiredSkills.count { it.candidateMatchStatus == CandidateMatchStatus.PARTIAL_GAP }
    val total = plan.requiredSkills.size
    return ((strong * 100 + partial * 60) / total).coerceIn(50, 99)
  }

  private fun computeFallbackExpScore(plan: JobActionPlan): Int {
    val jd = plan.jobDescription.lowercase()
    return when {
      jd.contains("dark store") || jd.contains("lead") -> 92
      jd.contains("fintech") || jd.contains("payment") -> 88
      jd.contains("consulting") || jd.contains("mece") -> 85
      else -> 86
    }
  }

  private fun computeAtsScreeningScore(plan: JobActionPlan, match: ResumeJdMatchResult?): Int {
    if (match != null) {
      val ratio = (match.matchedCount.toFloat() / match.totalJdSkillsCount.coerceAtLeast(1).toFloat())
      return (ratio * 100).toInt().coerceIn(60, 98)
    }
    val count = plan.requiredSkills.size.coerceAtLeast(1)
    val matched = plan.requiredSkills.count { it.candidateMatchStatus == CandidateMatchStatus.STRONG_MATCH }
    return ((matched.toFloat() / count.toFloat()) * 100).toInt().coerceIn(65, 96)
  }
}
