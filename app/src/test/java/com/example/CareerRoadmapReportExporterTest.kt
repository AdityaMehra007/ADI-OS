package com.example

import com.example.data.export.CareerRoadmapReportExporter
import com.example.data.export.ExportReportFormat
import com.example.data.export.ExportReportScope
import com.example.data.model.Application
import com.example.data.model.CareerGrowthMilestone
import com.example.data.model.ComprehensiveCareerRoadmapReport
import com.example.data.model.MilestoneGrowthPhase
import com.example.data.model.PipelineAnalysisMetrics
import com.example.data.model.SkillGapItem
import com.example.data.model.SkillGapSeverity
import com.example.data.model.SkillGapStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CareerRoadmapReportExporterTest {

  private fun createSampleReport(): ComprehensiveCareerRoadmapReport {
    val skillGaps = listOf(
      SkillGapItem(
        id = "gap_1",
        skillName = "Advanced Financial Modeling",
        category = "Strategic Finance",
        severity = SkillGapSeverity.CRITICAL,
        candidateLevel = "Intermediate",
        requiredLevel = "Advanced",
        demandedByCompanies = listOf("Bain & Co", "McKinsey"),
        affectedApplicationsCount = 3,
        relevanceRationale = "Required for commercial due diligence modeling",
        recommendedProjects = listOf("Build 3-statement LBO model for quick-commerce acquisition"),
        status = SkillGapStatus.IN_PROGRESS,
        progress = 60
      ),
      SkillGapItem(
        id = "gap_2",
        skillName = "AI Automation & Prompt Architecture",
        category = "AI Operations",
        severity = SkillGapSeverity.COMPETITIVE_MOAT,
        candidateLevel = "Advanced",
        requiredLevel = "Expert",
        demandedByCompanies = listOf("Google", "Swiggy"),
        affectedApplicationsCount = 5,
        relevanceRationale = "Strategic differentiator for operational scale",
        recommendedProjects = listOf("Deploy multi-agent job application orchestrator"),
        status = SkillGapStatus.CLOSED,
        progress = 100
      )
    )

    val milestones = listOf(
      CareerGrowthMilestone(
        id = "m1",
        title = "Build 3-Statement Financial Model",
        timeEstimate = "2 weeks",
        focusArea = "Technical Finance",
        keyDeliverable = "Complete DCF & LBO template",
        verificationCriteria = "Accurate sensitivity matrix verified",
        proofOfWorkArtifact = "GitHub repo: adi-fin-model-v1",
        unlocksRoles = listOf("Strategy Analyst", "Associate"),
        isCompleted = true,
        progress = 100
      ),
      CareerGrowthMilestone(
        id = "m2",
        title = "Deploy LangChain Workflow",
        timeEstimate = "3 weeks",
        focusArea = "AI Operations",
        keyDeliverable = "Production pipeline with Gemini",
        verificationCriteria = "Automates application scraping with 99% uptime",
        proofOfWorkArtifact = "Deployed microservice endpoint",
        unlocksRoles = listOf("AI Operations Lead"),
        isCompleted = false,
        progress = 50
      )
    )

    val phases = listOf(
      MilestoneGrowthPhase(
        phaseId = "phase_1",
        phaseNumber = 1,
        title = "Core Foundations & Valuation Mastery",
        timeframe = "Months 1 - 3",
        targetRoleTier = "Tier-1 Associate",
        projectedCompensation = "₹18L - ₹24L CTC",
        strategicFocus = "Establish hard analytical parity",
        targetCompaniesUnlocked = listOf("Bain", "Swiggy"),
        skillGapsTargeted = listOf("Advanced Financial Modeling"),
        milestones = milestones
      )
    )

    val metrics = PipelineAnalysisMetrics(
      savedCompaniesCount = 8,
      activeApplicationsCount = 4,
      interviewStageCount = 2,
      topRequiredSkillsAcrossCompanies = listOf("Financial Modeling", "AI Automation"),
      mostDemandedCategory = "Strategy & Ops",
      executiveMarketDiagnosis = "Rapidly surging demand for tech-literate strategy analysts.",
      strategicAdvantageIdentified = "Uncommon dual fluency in business acumen and modern AI tools."
    )

    return ComprehensiveCareerRoadmapReport(
      id = "roadmap_test_1",
      targetRole = "Senior Business & Operations Strategist",
      horizonYears = 2,
      generatedAt = "2026-09-16",
      isLiveGeminiGrounded = true,
      metrics = metrics,
      skillGaps = skillGaps,
      growthPhases = phases,
      highLeverageTactics = listOf(
        "Leverage cold outreach to alumni leads with portfolio projects.",
        "Demonstrate working AI prototypes in interview presentations."
      ),
      geminiExecutiveVerdict = "Strong competitive positioning with actionable path to high-growth leadership roles."
    )
  }

  private fun createSampleApplications(): List<Application> {
    return listOf(
      Application(
        id = "app_1",
        jobId = "job_1",
        companyName = "Google",
        roleTitle = "Strategy & BizOps Analyst",
        status = "OFFER",
        dateDiscovered = "2026-08-01",
        dateApplied = "2026-08-05",
        resumeVersion = "v2.1_Strategy",
        coverLetterSnippet = "Excited to bring AI leverage to Google operations...",
        customAnswersJson = "{}",
        followUpDate = "2026-09-20",
        followUpNotes = "Review compensation package and bonus tier",
        outcomeNotes = "Offer received: ₹22L CTC + ESOPs",
        rejectionAnalysis = "",
        interviewScore = 95
      ),
      Application(
        id = "app_2",
        jobId = "job_2",
        companyName = "Bain & Company",
        roleTitle = "Associate Consultant",
        status = "INTERVIEW",
        dateDiscovered = "2026-08-10",
        dateApplied = "2026-08-12",
        resumeVersion = "v2.1_Consulting",
        coverLetterSnippet = "Consulting problem solving background...",
        customAnswersJson = "{}",
        followUpDate = "2026-09-18",
        followUpNotes = "Final round partner interview on market entry case",
        outcomeNotes = "",
        rejectionAnalysis = "",
        interviewScore = 88
      ),
      Application(
        id = "app_3",
        jobId = "job_3",
        companyName = "Swiggy",
        roleTitle = "Product Operations Associate",
        status = "APPLIED",
        dateDiscovered = "2026-08-20",
        dateApplied = "2026-08-22",
        resumeVersion = "v2.0_Standard",
        coverLetterSnippet = "Operations optimization interest...",
        customAnswersJson = "{}",
        followUpDate = "2026-09-25",
        followUpNotes = "Follow up with recruiter after 14 days",
        outcomeNotes = "",
        rejectionAnalysis = "",
        interviewScore = 0
      )
    )
  }

  @Test
  fun testFullDossierMarkdownGeneration() {
    val report = createSampleReport()
    val apps = createSampleApplications()

    val markdown = CareerRoadmapReportExporter.generateReport(
      roadmapReport = report,
      applications = apps,
      candidateName = "Adi",
      scope = ExportReportScope.FULL_DOSSIER,
      format = ExportReportFormat.MARKDOWN
    )

    assertNotNull(markdown)
    assertTrue(markdown.contains("# 🚀 EXECUTIVE CAREER STRATEGY & PIPELINE REPORT"))
    assertTrue(markdown.contains("Senior Business & Operations Strategist"))
    assertTrue(markdown.contains("Advanced Financial Modeling"))
    assertTrue(markdown.contains("Google"))
    assertTrue(markdown.contains("Bain & Company"))
    assertTrue(markdown.contains("Swiggy"))
    assertTrue(markdown.contains("OFFER"))
    assertTrue(markdown.contains("INTERVIEW"))
    assertTrue(markdown.contains("Live Gemini 3.5 Flash Grounded"))
  }

  @Test
  fun testPlainTextGeneration() {
    val report = createSampleReport()
    val apps = createSampleApplications()

    val plainText = CareerRoadmapReportExporter.generateReport(
      roadmapReport = report,
      applications = apps,
      candidateName = "Adi",
      scope = ExportReportScope.FULL_DOSSIER,
      format = ExportReportFormat.PLAIN_TEXT
    )

    assertNotNull(plainText)
    assertTrue(plainText.contains("TITAN EXECUTIVE CAREER STRATEGY & PIPELINE REPORT"))
    assertTrue(plainText.contains("Candidate: Adi"))
    assertTrue(plainText.contains("PART 1: CAREER GROWTH ROADMAP"))
    assertTrue(plainText.contains("PART 2: JOB APPLICATIONS & PIPELINE STATUS LIST"))
    assertTrue(plainText.contains("Google - Strategy & BizOps Analyst"))
  }

  @Test
  fun testRoadmapOnlyScope() {
    val report = createSampleReport()
    val apps = createSampleApplications()

    val markdown = CareerRoadmapReportExporter.generateReport(
      roadmapReport = report,
      applications = apps,
      candidateName = "Adi",
      scope = ExportReportScope.ROADMAP_ONLY,
      format = ExportReportFormat.MARKDOWN
    )

    assertTrue(markdown.contains("PART 1: CAREER ROADMAP & STRATEGIC TRAJECTORY"))
    assertFalse(markdown.contains("PART 2: JOB APPLICATIONS & PIPELINE STATUS"))
  }

  @Test
  fun testJobStatusOnlyScope() {
    val report = createSampleReport()
    val apps = createSampleApplications()

    val markdown = CareerRoadmapReportExporter.generateReport(
      roadmapReport = report,
      applications = apps,
      candidateName = "Adi",
      scope = ExportReportScope.JOB_STATUS_ONLY,
      format = ExportReportFormat.MARKDOWN
    )

    assertFalse(markdown.contains("PART 1: CAREER ROADMAP & STRATEGIC TRAJECTORY"))
    assertTrue(markdown.contains("PART 2: JOB APPLICATIONS & PIPELINE STATUS"))
    assertTrue(markdown.contains("Google"))
  }

  @Test
  fun testNullRoadmapHandling() {
    val apps = createSampleApplications()

    val markdown = CareerRoadmapReportExporter.generateReport(
      roadmapReport = null,
      applications = apps,
      candidateName = "Adi",
      scope = ExportReportScope.FULL_DOSSIER,
      format = ExportReportFormat.MARKDOWN
    )

    assertNotNull(markdown)
    assertTrue(markdown.contains("Google"))
    assertTrue(markdown.contains("No active career roadmap generated yet"))
  }
}
