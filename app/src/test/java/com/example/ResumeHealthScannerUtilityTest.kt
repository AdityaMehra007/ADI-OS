package com.example

import com.example.util.HealthCategory
import com.example.util.IndustryBenchmark
import com.example.util.ResumeHealthScannerUtility
import com.example.util.SuggestionPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResumeHealthScannerUtilityTest {

  @Test
  fun testScanResume_SeniorProfile_ReturnsHighHealthScore() {
    val seniorResume = ResumeHealthScannerUtility.PRESET_RESUMES.first().sampleText
    val report = ResumeHealthScannerUtility.scanResume(
      seniorResume,
      IndustryBenchmark.MOBILE_ANDROID
    )

    assertNotNull(report)
    assertTrue("Overall score should be high (>= 75)", report.overallHealthScore >= 75)
    assertTrue("Letter grade should be A or B+", report.letterGrade in listOf("A+", "A", "B+"))
    assertTrue("Should detect multiple power verbs", report.powerVerbsFound.isNotEmpty())
    assertTrue("Should have quantified bullets", report.quantifiedBulletsCount > 0)
    assertTrue("Should match Android keywords", report.detectedIndustryKeywords.contains("Kotlin"))
    assertTrue("Subscores should cover 5 pillars", report.subScores.size == 5)
  }

  @Test
  fun testScanResume_EarlyCareerWithGaps_DetectsClichésAndLowQuantification() {
    val weakResume = ResumeHealthScannerUtility.PRESET_RESUMES[1].sampleText
    val report = ResumeHealthScannerUtility.scanResume(
      weakResume,
      IndustryBenchmark.TECH_DISTRIBUTED_SYSTEMS
    )

    assertNotNull(report)
    assertTrue("Should flag clichés like hardworking or team player", report.clichesFound.isNotEmpty())
    assertTrue("Should flag passive phrases", report.weakVerbsFound.isNotEmpty())
    assertTrue("Should generate critical or high priority suggestions", report.suggestions.any {
      it.priority == SuggestionPriority.CRITICAL || it.priority == SuggestionPriority.HIGH
    })
    assertTrue("Should propose bullet transformations", report.bulletTransformations.isNotEmpty())
  }

  @Test
  fun testSubscores_CategoriesAreDistinct() {
    val report = ResumeHealthScannerUtility.scanResume(
      "Senior engineer who architected microservices.",
      IndustryBenchmark.TECH_DISTRIBUTED_SYSTEMS
    )

    val categories = report.subScores.map { it.category }
    assertEquals(5, categories.toSet().size)
    assertTrue(categories.contains(HealthCategory.METRICS_IMPACT))
    assertTrue(categories.contains(HealthCategory.ACTION_VERBS))
    assertTrue(categories.contains(HealthCategory.ATS_COMPLIANCE))
    assertTrue(categories.contains(HealthCategory.INDUSTRY_KEYWORDS))
    assertTrue(categories.contains(HealthCategory.CLICHE_ELIMINATION))
  }
}
