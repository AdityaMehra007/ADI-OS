package com.example

import com.example.data.model.CandidateMatchStatus
import com.example.service.JobSplitComparisonService
import com.example.service.LocalDocumentParser
import com.example.service.LocalJobActionPlanGenerator
import com.example.service.ResumeDocumentParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JobSplitComparisonTest {

  @Test
  fun testPresetPairsExist() {
    val presets = JobSplitComparisonService.PRESET_COMPARISON_PAIRS
    assertTrue("Presets must not be empty", presets.isNotEmpty())
    assertTrue("Should include Zepto vs Blinkit preset", presets.any { it.id == "preset_zepto_blinkit" })
    assertTrue("Should include Zepto vs Razorpay preset", presets.any { it.id == "preset_zepto_razorpay" })
  }

  @Test
  fun testSplitComparisonReportGeneration() {
    val zeptoSample = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS.first { it.company == "Zepto" }
    val razorpaySample = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS.first { it.company == "Razorpay" }

    val planA = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = zeptoSample.role,
      targetCompany = zeptoSample.company,
      jobDescription = zeptoSample.description
    )
    val planB = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = razorpaySample.role,
      targetCompany = razorpaySample.company,
      jobDescription = razorpaySample.description
    )

    val resume = LocalDocumentParser.parseDocumentText(
      fileName = "Aditya_Mehra_Operations_Lead_v3.pdf",
      rawText = LocalDocumentParser.AVAILABLE_MOCK_DOCUMENTS.first().rawContent
    )

    val matchA = ResumeDocumentParser.compareResumeWithJobDescription(
      resume = resume,
      jobDescription = zeptoSample.description,
      targetRole = zeptoSample.role,
      targetCompany = zeptoSample.company
    )

    val matchB = ResumeDocumentParser.compareResumeWithJobDescription(
      resume = resume,
      jobDescription = razorpaySample.description,
      targetRole = razorpaySample.role,
      targetCompany = razorpaySample.company
    )

    val report = JobSplitComparisonService.buildComparisonReport(
      jobA = planA,
      matchA = matchA,
      jobB = planB,
      matchB = matchB
    )

    assertNotNull("Report must be non-null", report)
    assertEquals("Zepto", report.jobA.targetCompany)
    assertEquals("Razorpay", report.jobB.targetCompany)
    assertTrue("Score A must be within 0-100", report.scoreA in 0..100)
    assertTrue("Score B must be within 0-100", report.scoreB in 0..100)
    assertEquals(report.scoreA - report.scoreB, report.scoreDelta)
    assertTrue("Winning margin must describe the delta", report.winningMarginSummary.isNotBlank())
    assertTrue("Strategic takeaway must provide actionable comparison", report.strategicTakeaway.isNotBlank())
    assertTrue("Recommended primary target must not be blank", report.recommendedPrimaryTarget.isNotBlank())

    // Validate 5 comparison dimensions
    assertEquals("Should generate 5 distinct comparison dimensions", 5, report.dimensions.size)
    val dimKeys = report.dimensions.map { it.categoryKey }
    assertTrue("Must contain OVERALL dimension", dimKeys.contains("OVERALL"))
    assertTrue("Must contain SKILLS dimension", dimKeys.contains("SKILLS"))
    assertTrue("Must contain EXPERIENCE dimension", dimKeys.contains("EXPERIENCE"))
    assertTrue("Must contain METRICS dimension", dimKeys.contains("METRICS"))
    assertTrue("Must contain ATS dimension", dimKeys.contains("ATS"))

    report.dimensions.forEach { dim ->
      assertTrue("Dimension title must be non-empty", dim.dimensionTitle.isNotBlank())
      assertTrue("Score A must be valid", dim.scoreA in 0..100)
      assertTrue("Score B must be valid", dim.scoreB in 0..100)
      assertTrue("Metric label A must be non-empty", dim.metricLabelA.isNotBlank())
      assertTrue("Metric label B must be non-empty", dim.metricLabelB.isNotBlank())
      assertTrue("Insight text must explain the dimension", dim.insightText.isNotBlank())
    }
  }

  @Test
  fun testSplitComparisonWithFallbackCalculations() {
    val zeptoSample = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS.first { it.company == "Zepto" }
    val blinkitSample = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS.first { it.company == "Blinkit" }

    val planA = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = zeptoSample.role,
      targetCompany = zeptoSample.company,
      jobDescription = zeptoSample.description
    )
    val planB = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = blinkitSample.role,
      targetCompany = blinkitSample.company,
      jobDescription = blinkitSample.description
    )

    // Verify when match results are null (fallback to plan scores)
    val report = JobSplitComparisonService.buildComparisonReport(
      jobA = planA,
      matchA = null,
      jobB = planB,
      matchB = null
    )

    assertNotNull(report)
    assertEquals(planA.matchFitScore, report.scoreA)
    assertEquals(planB.matchFitScore, report.scoreB)
    assertTrue("Dimensions should still be computed with fallbacks", report.dimensions.isNotEmpty())
    assertEquals(5, report.dimensions.size)
  }
}
