package com.example

import com.example.data.model.CareerVelocityReport
import com.example.data.model.SkillsRadarReport
import com.example.data.repository.CareerVelocityDataProvider
import com.example.service.LocalDocumentParser
import com.example.service.SkillsRadarEngine
import com.example.service.WeeklyCareerHealthReportService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeeklyCareerHealthReportTest {

  @Test
  fun testSynthesizeWeeklyReport_GeneratesValidScorecard() {
    val resume = LocalDocumentParser.AVAILABLE_MOCK_DOCUMENTS.first()
    val targetJob = SkillsRadarEngine.TARGET_JOB_PROFILES.first()

    val radarReport = SkillsRadarEngine.generateSkillsRadarReport(
      resume = resume,
      jobProfile = targetJob,
      injectedKeywords = emptySet(),
      injectedCertifications = emptySet()
    )

    val velocityReport = CareerVelocityDataProvider.getDefaultCareerVelocityReport()

    val weeklyReport = WeeklyCareerHealthReportService.synthesizeWeeklyReport(
      skillsRadar = radarReport,
      velocityReport = velocityReport,
      candidateName = "Aditya Mehra",
      candidateRole = "Lead Strategy & Operations"
    )

    assertNotNull(weeklyReport)
    assertEquals("Aditya Mehra", weeklyReport.candidateName)
    assertEquals("Lead Strategy & Operations", weeklyReport.candidateRole)
    assertEquals(targetJob.roleTitle, weeklyReport.targetBenchmarkRole)
    assertEquals(targetJob.companyName, weeklyReport.targetBenchmarkCompany)

    // Score verification
    assertTrue("Composite score should be > 0 and <= 100", weeklyReport.compositeHealthScore in 1..100)
    assertTrue("Health grade should not be blank", weeklyReport.healthGrade.isNotBlank())
    assertTrue("Sprint objectives should contain items", weeklyReport.weeklySprintObjectives.isNotEmpty())
    assertTrue("Executive narrative should not be blank", weeklyReport.executiveSummaryNarrative.isNotBlank())

    // Dimension breakdown
    assertEquals(6, weeklyReport.dimensionScores.size)
  }

  @Test
  fun testSprintObjectives_PresentsActionableInsights() {
    val resume = LocalDocumentParser.AVAILABLE_MOCK_DOCUMENTS.first()
    val targetJob = SkillsRadarEngine.TARGET_JOB_PROFILES.first()

    val radarReport = SkillsRadarEngine.generateSkillsRadarReport(
      resume = resume,
      jobProfile = targetJob,
      injectedKeywords = emptySet(),
      injectedCertifications = emptySet()
    )

    val velocityReport = CareerVelocityDataProvider.getDefaultCareerVelocityReport()

    val weeklyReport = WeeklyCareerHealthReportService.synthesizeWeeklyReport(
      skillsRadar = radarReport,
      velocityReport = velocityReport
    )

    weeklyReport.weeklySprintObjectives.forEach { objective ->
      assertTrue(objective.priorityRank > 0)
      assertTrue(objective.title.isNotBlank())
      assertTrue(objective.estimatedImpact.isNotBlank())
      assertFalse(objective.isCompleted)
    }
  }
}
