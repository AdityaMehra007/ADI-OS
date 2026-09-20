package com.example.data.export

import com.example.data.model.Application
import com.example.data.model.ComprehensiveCareerRoadmapReport
import com.example.data.model.SkillGapItem
import com.example.data.model.SkillGapSeverity
import com.example.data.model.SkillGapStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Scope selector for the exported career report
 */
enum class ExportReportScope(val displayName: String, val subtitle: String) {
  FULL_DOSSIER("Full Dossier (Roadmap & Jobs)", "Comprehensive career strategy & active application roster"),
  ROADMAP_ONLY("Career Roadmap Only", "Skill gaps, milestone phases & executive strategy"),
  JOB_STATUS_ONLY("Job Status Pipeline Only", "Active job applications, stages & follow-ups")
}

/**
 * Format selector for the exported career report
 */
enum class ExportReportFormat(val displayName: String, val extension: String) {
  MARKDOWN("Formatted Markdown", ".md"),
  PLAIN_TEXT("Clean Plain Text", ".txt")
}

/**
 * Utility service to generate formatted summary text and simple reports
 * for the current career roadmap and job status lists.
 */
object CareerRoadmapReportExporter {

  /**
   * Generates the requested report string according to scope and format
   */
  fun generateReport(
    roadmapReport: ComprehensiveCareerRoadmapReport?,
    applications: List<Application>,
    candidateName: String = "Candidate",
    scope: ExportReportScope = ExportReportScope.FULL_DOSSIER,
    format: ExportReportFormat = ExportReportFormat.MARKDOWN
  ): String {
    val dateStr = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date())

    return when (format) {
      ExportReportFormat.MARKDOWN -> generateMarkdownReport(roadmapReport, applications, candidateName, scope, dateStr)
      ExportReportFormat.PLAIN_TEXT -> generatePlainTextReport(roadmapReport, applications, candidateName, scope, dateStr)
    }
  }

  // -----------------------------------------------------------------------------------
  // Markdown Report Generation
  // -----------------------------------------------------------------------------------

  private fun generateMarkdownReport(
    roadmap: ComprehensiveCareerRoadmapReport?,
    applications: List<Application>,
    candidateName: String,
    scope: ExportReportScope,
    dateStr: String
  ): String {
    val sb = StringBuilder()

    // Title & Header
    sb.appendLine("# 🚀 EXECUTIVE CAREER STRATEGY & PIPELINE REPORT")
    sb.appendLine("**Candidate:** $candidateName | **Generated:** $dateStr")
    if (roadmap != null) {
      sb.appendLine("**Target Role:** ${roadmap.targetRole} (${roadmap.horizonYears}-Year Horizon)")
      sb.appendLine("**Intelligence Engine:** ${if (roadmap.isLiveGeminiGrounded) "Live Gemini 3.5 Flash Grounded" else "Titan Strategic Intelligence"}")
    }
    sb.appendLine()
    sb.appendLine("---")
    sb.appendLine()

    // High Level KPI Summary Box
    val totalApps = applications.size
    val activeOffers = applications.count { it.status.equals("OFFER", ignoreCase = true) }
    val interviewing = applications.count {
      it.status.contains("INTERVIEW", ignoreCase = true) ||
        it.status.contains("FINAL", ignoreCase = true) ||
        it.status.contains("ASSESSMENT", ignoreCase = true)
    }
    val appliedInReview = applications.count {
      it.status.equals("APPLIED", ignoreCase = true) ||
        it.status.contains("VIEW", ignoreCase = true) ||
        it.status.contains("SHORTLIST", ignoreCase = true)
    }

    sb.appendLine("## 📊 EXECUTIVE SUMMARY & PIPELINE KPIS")
    sb.appendLine("| Metric | Status | Details |")
    sb.appendLine("| :--- | :--- | :--- |")
    if (roadmap != null) {
      sb.appendLine("| **Roadmap Velocity** | `${roadmap.overallProgressPercentage}%` | ${roadmap.completedMilestonesCount}/${roadmap.totalMilestonesCount} Milestones Completed |")
      sb.appendLine("| **Skill Gaps Closed** | `${roadmap.closedSkillGapsCount}/${roadmap.skillGaps.size}` | ${(roadmap.closedSkillGapsCount * 100 / roadmap.skillGaps.size.coerceAtLeast(1))}% Closed |")
    }
    sb.appendLine("| **Active Pipeline** | `${totalApps} Applications` | Tracked Across High-Growth Companies |")
    sb.appendLine("| **Offers Secured** | `${activeOffers} Offers` | Active Compensation Negotiation |")
    sb.appendLine("| **Interview Stages** | `${interviewing} Active Rounds` | Assessments & Technical Interviews |")
    sb.appendLine("| **In Review** | `${appliedInReview} Applications` | Applied / Recruiter Screening |")
    sb.appendLine()

    // SECTION 1: CAREER ROADMAP
    if (scope == ExportReportScope.FULL_DOSSIER || scope == ExportReportScope.ROADMAP_ONLY) {
      sb.appendLine("---")
      sb.appendLine("## 🎯 PART 1: CAREER ROADMAP & STRATEGIC TRAJECTORY")
      sb.appendLine()

      if (roadmap != null) {
        // Executive Diagnosis
        sb.appendLine("### 🧠 Strategic Market Diagnosis")
        sb.appendLine("> \"${roadmap.geminiExecutiveVerdict}\"")
        sb.appendLine()
        if (roadmap.metrics.executiveMarketDiagnosis.isNotBlank()) {
          sb.appendLine("**Market Context:** ${roadmap.metrics.executiveMarketDiagnosis}")
        }
        if (roadmap.metrics.strategicAdvantageIdentified.isNotBlank()) {
          sb.appendLine("**Identified Competitive Edge:** ${roadmap.metrics.strategicAdvantageIdentified}")
        }
        sb.appendLine()

        // Identified Skill Gaps
        sb.appendLine("### ⚡ Competency Gaps & Parity Analysis")
        if (roadmap.skillGaps.isEmpty()) {
          sb.appendLine("_No critical skill deficits currently flagged._")
        } else {
          sb.appendLine("| Priority | Skill Name | Domain | Current Level | Target Standard | Demanding Companies | Progress |")
          sb.appendLine("| :--- | :--- | :--- | :--- | :--- | :--- | :--- |")
          roadmap.skillGaps.forEach { gap ->
            val prioBadge = when (gap.severity) {
              SkillGapSeverity.CRITICAL -> "🔴 CRITICAL"
              SkillGapSeverity.HIGH_PRIORITY -> "🟡 HIGH"
              SkillGapSeverity.COMPETITIVE_MOAT -> "🟢 MOAT"
            }
            val companiesStr = if (gap.demandedByCompanies.isNotEmpty()) gap.demandedByCompanies.joinToString(", ") else "Pipeline Wide"
            sb.appendLine("| $prioBadge | **${gap.skillName}** | ${gap.category} | ${gap.candidateLevel} | ${gap.requiredLevel} | $companiesStr | `${gap.progress}%` |")
          }
        }
        sb.appendLine()

        // Growth Milestone Phases
        sb.appendLine("### 📈 Sequential Growth Phases")
        roadmap.growthPhases.forEach { phase ->
          sb.appendLine("#### Phase ${phase.phaseNumber}: ${phase.title} (${phase.timeframe})")
          sb.appendLine("- **Target Role Tier:** ${phase.targetRoleTier}")
          sb.appendLine("- **Target Compensation:** ${phase.projectedCompensation}")
          sb.appendLine("- **Strategic Focus:** ${phase.strategicFocus}")
          sb.appendLine("- **Phase Completion:** `${phase.completionPercentage}%`")
          if (phase.targetCompaniesUnlocked.isNotEmpty()) {
            sb.appendLine("- **Target Companies Unlocked:** ${phase.targetCompaniesUnlocked.joinToString(", ")}")
          }
          sb.appendLine()
          sb.appendLine("**Key Milestones & Deliverables:**")
          phase.milestones.forEach { m ->
            val check = if (m.isCompleted || m.progress >= 100) "[x]" else "[ ]"
            sb.appendLine("  $check **${m.title}** (${m.timeEstimate}) - Progress: `${m.progress}%`")
            sb.appendLine("    - *Deliverable:* ${m.keyDeliverable}")
            sb.appendLine("    - *Verification:* ${m.verificationCriteria}")
          }
          sb.appendLine()
        }

        // High Leverage Tactics
        if (roadmap.highLeverageTactics.isNotEmpty()) {
          sb.appendLine("### 💡 High-Leverage Strategic Tactics")
          roadmap.highLeverageTactics.forEachIndexed { idx, tactic ->
            sb.appendLine("${idx + 1}. $tactic")
          }
          sb.appendLine()
        }
      } else {
        sb.appendLine("_No active career roadmap generated yet. Recalibrate in the Career Strategy tab to build a full plan._")
        sb.appendLine()
      }
    }

    // SECTION 2: JOB APPLICATION STATUS LISTS
    if (scope == ExportReportScope.FULL_DOSSIER || scope == ExportReportScope.JOB_STATUS_ONLY) {
      sb.appendLine("---")
      sb.appendLine("## 💼 PART 2: JOB APPLICATIONS & PIPELINE STATUS")
      sb.appendLine()

      if (applications.isEmpty()) {
        sb.appendLine("_No job applications currently tracked in the database._")
      } else {
        // Group by status category
        val offers = applications.filter { it.status.equals("OFFER", ignoreCase = true) }
        val interviews = applications.filter {
          it.status.contains("INTERVIEW", ignoreCase = true) ||
            it.status.contains("FINAL", ignoreCase = true) ||
            it.status.contains("ASSESSMENT", ignoreCase = true)
        }
        val applied = applications.filter {
          it.status.equals("APPLIED", ignoreCase = true) ||
            it.status.contains("VIEW", ignoreCase = true) ||
            it.status.contains("SHORTLIST", ignoreCase = true)
        }
        val rejectedOrClosed = applications.filter {
          it.status.equals("REJECTED", ignoreCase = true) ||
            it.status.equals("WITHDRAWN", ignoreCase = true) ||
            it.status.equals("CLOSED", ignoreCase = true)
        }
        val others = applications.filter {
          !offers.contains(it) && !interviews.contains(it) && !applied.contains(it) && !rejectedOrClosed.contains(it)
        }

        if (offers.isNotEmpty()) {
          sb.appendLine("### 🏆 Offers Received (${offers.size})")
          renderApplicationMarkdownTable(sb, offers)
          sb.appendLine()
        }

        if (interviews.isNotEmpty()) {
          sb.appendLine("### 🎯 Interview & Assessment Stages (${interviews.size})")
          renderApplicationMarkdownTable(sb, interviews)
          sb.appendLine()
        }

        if (applied.isNotEmpty()) {
          sb.appendLine("### 📬 Applied & Under Review (${applied.size})")
          renderApplicationMarkdownTable(sb, applied)
          sb.appendLine()
        }

        if (others.isNotEmpty()) {
          sb.appendLine("### 📋 Discovered & In Preparation (${others.size})")
          renderApplicationMarkdownTable(sb, others)
          sb.appendLine()
        }

        if (rejectedOrClosed.isNotEmpty()) {
          sb.appendLine("### 📁 Closed / Archived Applications (${rejectedOrClosed.size})")
          renderApplicationMarkdownTable(sb, rejectedOrClosed)
          sb.appendLine()
        }
      }
    }

    sb.appendLine("---")
    sb.appendLine("*Exported from Titan Career Intelligence • Powered by Gemini AI*")

    return sb.toString()
  }

  private fun renderApplicationMarkdownTable(sb: StringBuilder, list: List<Application>) {
    sb.appendLine("| Company | Role | Status | Applied Date | Follow-up | Notes / Feedback |")
    sb.appendLine("| :--- | :--- | :--- | :--- | :--- | :--- |")
    list.forEach { app ->
      val appliedDate = if (app.dateApplied.isNotBlank()) app.dateApplied else (if (app.dateDiscovered.isNotBlank()) app.dateDiscovered else "-")
      val followUp = if (app.followUpDate.isNotBlank()) app.followUpDate else "-"
      val note = when {
        app.outcomeNotes.isNotBlank() -> app.outcomeNotes.replace("\n", " ")
        app.followUpNotes.isNotBlank() -> app.followUpNotes.replace("\n", " ")
        app.rejectionAnalysis.isNotBlank() -> app.rejectionAnalysis.replace("\n", " ")
        else -> "-"
      }
      val truncatedNote = if (note.length > 60) note.take(57) + "..." else note
      sb.appendLine("| **${app.companyName}** | ${app.roleTitle} | `${app.status}` | $appliedDate | $followUp | $truncatedNote |")
    }
  }

  // -----------------------------------------------------------------------------------
  // Plain Text Report Generation
  // -----------------------------------------------------------------------------------

  private fun generatePlainTextReport(
    roadmap: ComprehensiveCareerRoadmapReport?,
    applications: List<Application>,
    candidateName: String,
    scope: ExportReportScope,
    dateStr: String
  ): String {
    val sb = StringBuilder()
    val divider = "=".repeat(68)
    val subDivider = "-".repeat(68)

    sb.appendLine(divider)
    sb.appendLine("          TITAN EXECUTIVE CAREER STRATEGY & PIPELINE REPORT")
    sb.appendLine(divider)
    sb.appendLine("Candidate: $candidateName")
    sb.appendLine("Generated: $dateStr")
    if (roadmap != null) {
      sb.appendLine("Target Role: ${roadmap.targetRole} (${roadmap.horizonYears}-Year Horizon)")
      sb.appendLine("Engine: ${if (roadmap.isLiveGeminiGrounded) "Live Gemini 3.5 Flash Grounded" else "Titan Strategic Intelligence"}")
    }
    sb.appendLine()

    // KPI Summary
    val totalApps = applications.size
    val activeOffers = applications.count { it.status.equals("OFFER", ignoreCase = true) }
    val interviewing = applications.count {
      it.status.contains("INTERVIEW", ignoreCase = true) ||
        it.status.contains("FINAL", ignoreCase = true) ||
        it.status.contains("ASSESSMENT", ignoreCase = true)
    }
    val appliedInReview = applications.count {
      it.status.equals("APPLIED", ignoreCase = true) ||
        it.status.contains("VIEW", ignoreCase = true) ||
        it.status.contains("SHORTLIST", ignoreCase = true)
    }

    sb.appendLine("EXECUTIVE SUMMARY METRICS:")
    if (roadmap != null) {
      sb.appendLine("  * Overall Roadmap Velocity: ${roadmap.overallProgressPercentage}% (${roadmap.completedMilestonesCount}/${roadmap.totalMilestonesCount} Milestones Completed)")
      sb.appendLine("  * Skill Gaps Closed: ${roadmap.closedSkillGapsCount}/${roadmap.skillGaps.size}")
    }
    sb.appendLine("  * Total Applications Tracked: $totalApps")
    sb.appendLine("  * Active Job Offers: $activeOffers")
    sb.appendLine("  * Active Interview Rounds: $interviewing")
    sb.appendLine("  * In Review / Screening: $appliedInReview")
    sb.appendLine()

    // PART 1: CAREER ROADMAP
    if (scope == ExportReportScope.FULL_DOSSIER || scope == ExportReportScope.ROADMAP_ONLY) {
      sb.appendLine(divider)
      sb.appendLine("PART 1: CAREER GROWTH ROADMAP & SKILLS ANALYSIS")
      sb.appendLine(divider)
      sb.appendLine()

      if (roadmap != null) {
        sb.appendLine("EXECUTIVE VERDICT:")
        sb.appendLine("  \"${roadmap.geminiExecutiveVerdict}\"")
        sb.appendLine()

        if (roadmap.metrics.executiveMarketDiagnosis.isNotBlank()) {
          sb.appendLine("MARKET DIAGNOSIS: ${roadmap.metrics.executiveMarketDiagnosis}")
        }
        if (roadmap.metrics.strategicAdvantageIdentified.isNotBlank()) {
          sb.appendLine("COMPETITIVE EDGE: ${roadmap.metrics.strategicAdvantageIdentified}")
        }
        sb.appendLine()

        sb.appendLine(subDivider)
        sb.appendLine("IDENTIFIED SKILL GAPS & PARITY TRAJECTORY:")
        sb.appendLine(subDivider)
        if (roadmap.skillGaps.isEmpty()) {
          sb.appendLine("  No critical skill gaps identified.")
        } else {
          roadmap.skillGaps.forEachIndexed { i, gap ->
            val companies = if (gap.demandedByCompanies.isNotEmpty()) gap.demandedByCompanies.joinToString(", ") else "General"
            sb.appendLine("  [${gap.severity.displayName.uppercase()}] ${gap.skillName} (${gap.category})")
            sb.appendLine("    - Candidate Level: ${gap.candidateLevel}  ->  Required: ${gap.requiredLevel}")
            sb.appendLine("    - Progress: ${gap.progress}% (${gap.status.label})")
            sb.appendLine("    - Demanded By: $companies")
            if (gap.relevanceRationale.isNotBlank()) {
              sb.appendLine("    - Rationale: ${gap.relevanceRationale}")
            }
            sb.appendLine()
          }
        }

        sb.appendLine(subDivider)
        sb.appendLine("SEQUENTIAL GROWTH PHASES:")
        sb.appendLine(subDivider)
        roadmap.growthPhases.forEach { phase ->
          sb.appendLine("  PHASE ${phase.phaseNumber}: ${phase.title.uppercase()} (${phase.timeframe})")
          sb.appendLine("  Role Tier: ${phase.targetRoleTier} | Comp: ${phase.projectedCompensation}")
          sb.appendLine("  Strategic Focus: ${phase.strategicFocus}")
          sb.appendLine("  Phase Completion: ${phase.completionPercentage}%")
          if (phase.targetCompaniesUnlocked.isNotEmpty()) {
            sb.appendLine("  Companies Unlocked: ${phase.targetCompaniesUnlocked.joinToString(", ")}")
          }
          sb.appendLine("  Milestones:")
          phase.milestones.forEach { m ->
            val mark = if (m.isCompleted || m.progress >= 100) "[DONE]" else "[PENDING]"
            sb.appendLine("    $mark ${m.title} (${m.timeEstimate}) - ${m.progress}%")
            sb.appendLine("      Deliverable: ${m.keyDeliverable}")
            sb.appendLine("      Verification: ${m.verificationCriteria}")
          }
          sb.appendLine()
        }

        if (roadmap.highLeverageTactics.isNotEmpty()) {
          sb.appendLine(subDivider)
          sb.appendLine("HIGH-LEVERAGE TACTICS:")
          sb.appendLine(subDivider)
          roadmap.highLeverageTactics.forEachIndexed { idx, t ->
            sb.appendLine("  ${idx + 1}. $t")
          }
          sb.appendLine()
        }
      } else {
        sb.appendLine("No career roadmap generated yet.")
        sb.appendLine()
      }
    }

    // PART 2: JOB APPLICATION STATUS
    if (scope == ExportReportScope.FULL_DOSSIER || scope == ExportReportScope.JOB_STATUS_ONLY) {
      sb.appendLine(divider)
      sb.appendLine("PART 2: JOB APPLICATIONS & PIPELINE STATUS LIST")
      sb.appendLine(divider)
      sb.appendLine()

      if (applications.isEmpty()) {
        sb.appendLine("No applications currently tracked.")
      } else {
        applications.forEachIndexed { idx, app ->
          val applied = if (app.dateApplied.isNotBlank()) app.dateApplied else (if (app.dateDiscovered.isNotBlank()) app.dateDiscovered else "N/A")
          sb.appendLine("${idx + 1}. [${app.status}] ${app.companyName} - ${app.roleTitle}")
          sb.appendLine("   Date: $applied | Follow-up: ${if (app.followUpDate.isNotBlank()) app.followUpDate else "None"}")
          if (app.followUpNotes.isNotBlank()) {
            sb.appendLine("   Follow-up Notes: ${app.followUpNotes}")
          }
          if (app.outcomeNotes.isNotBlank()) {
            sb.appendLine("   Outcome Notes: ${app.outcomeNotes}")
          }
          if (app.rejectionAnalysis.isNotBlank()) {
            sb.appendLine("   Rejection Analysis: ${app.rejectionAnalysis}")
          }
          sb.appendLine()
        }
      }
    }

    sb.appendLine(divider)
    sb.appendLine("Report Generated by Titan Career Intelligence • AI Studio 2026")
    sb.appendLine(divider)

    return sb.toString()
  }
}
