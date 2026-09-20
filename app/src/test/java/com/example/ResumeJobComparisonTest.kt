package com.example

import com.example.data.model.ComparisonActionStep
import com.example.data.model.ComparisonBulletEnhancement
import com.example.data.model.MatchedRequirementItem
import com.example.data.model.RequirementGapItem
import com.example.data.model.ResumeJobComparisonFeedback
import com.example.data.model.ResumeJobComparisonRequest
import com.example.data.model.TargetCompanyCultureIntel
import com.example.service.ResumeJobComparisonService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResumeJobComparisonTest {

  @Test
  fun testPresetCompaniesCompleteness() {
    val presets = ResumeJobComparisonService.PRESET_COMPANIES
    assertTrue("Preset companies must not be empty", presets.isNotEmpty())
    assertTrue("Must have at least 5 target companies", presets.size >= 5)

    val companyNames = presets.map { it.company }
    assertTrue(companyNames.contains("Zepto"))
    assertTrue(companyNames.contains("Razorpay"))
    assertTrue(companyNames.contains("Swiggy Instamart"))
    assertTrue(companyNames.contains("CRED"))
    assertTrue(companyNames.contains("Zomato / Blinkit"))
    assertTrue(companyNames.contains("Flipkart"))

    presets.forEach { preset ->
      assertTrue("Role title cannot be blank for ${preset.company}", preset.role.isNotBlank())
      assertTrue("Requirements text cannot be blank for ${preset.company}", preset.requirementsText.isNotBlank())
      assertTrue("Logo emoji cannot be blank for ${preset.company}", preset.logoEmoji.isNotBlank())
      assertTrue("Focus areas cannot be empty for ${preset.company}", preset.keyFocusAreas.isNotEmpty())
    }
  }

  @Test
  fun testDefaultCandidateResumeContent() {
    val resume = ResumeJobComparisonService.DEFAULT_RESUME_TEXT
    assertTrue("Resume must contain candidate name", resume.contains("ADITYA \"ADI\" SHENOY"))
    assertTrue("Resume must contain 42% order cycle reduction metric", resume.contains("42%"))
    assertTrue("Resume must contain ₹18L enterprise pipeline metric", resume.contains("₹18L"))
    assertTrue("Resume must mention SQL competencies", resume.contains("SQL"))
    assertTrue("Resume must mention Python competencies", resume.contains("Python"))
    assertTrue("Resume must mention dark store / fulfillment operations", resume.contains("fulfillment") || resume.contains("Dark Store"))
  }

  @Test
  fun testDeterministicComparisonForZepto() {
    val zeptoPreset = ResumeJobComparisonService.PRESET_COMPANIES.first { it.company == "Zepto" }
    val request = ResumeJobComparisonRequest(
      resumeText = ResumeJobComparisonService.DEFAULT_RESUME_TEXT,
      targetCompany = zeptoPreset.company,
      targetRole = zeptoPreset.role,
      jobRequirements = zeptoPreset.requirementsText
    )

    val feedback = ResumeJobComparisonService.generateDeterministicComparison(request)

    assertEquals("Zepto", feedback.targetCompany)
    assertEquals(zeptoPreset.role, feedback.targetRole)
    assertTrue("Overall fit score should be high for candidate", feedback.overallFitScore >= 80)
    assertTrue("ATS compatibility score should be above 85%", feedback.atsCompatibilityScore >= 85)
    assertTrue("Readiness verdict must not be blank", feedback.readinessVerdict.isNotBlank())
    assertTrue("Executive summary must not be blank", feedback.executiveSummary.isNotBlank())

    // Matched requirements check
    assertTrue("Must find SQL or SLA matches in candidate resume", feedback.matchedRequirements.isNotEmpty())
    val hasSlaMatch = feedback.matchedRequirements.any { it.requirementTitle.contains("SLA") || it.requirementTitle.contains("Turnaround") }
    assertTrue("Should detect 42% turnaround SLA match", hasSlaMatch)

    // Culture bar raiser check
    assertEquals("Zepto", feedback.companyCultureIntel.companyName)
    assertTrue(feedback.companyCultureIntel.barRaiserFocus.contains("Speed") || feedback.companyCultureIntel.barRaiserFocus.contains("Floor Reality"))
    assertTrue(feedback.companyCultureIntel.commonInterviewTraps.isNotEmpty())

    // Bullet enhancements check
    assertTrue("Must generate bullet rewrites", feedback.bulletEnhancements.isNotEmpty())
    val firstEnhancement = feedback.bulletEnhancements.first()
    assertTrue(firstEnhancement.originalBullet.isNotBlank())
    assertTrue(firstEnhancement.enhancedBullet.isNotBlank())
    assertTrue(firstEnhancement.metricInfused.isNotBlank())

    // Action plan check
    assertTrue("Must have action steps", feedback.actionPlan.isNotEmpty())
    assertEquals(1, feedback.actionPlan.first().priorityOrder)
  }

  @Test
  fun testDeterministicComparisonForRazorpay() {
    val razorpayPreset = ResumeJobComparisonService.PRESET_COMPANIES.first { it.company == "Razorpay" }
    val request = ResumeJobComparisonRequest(
      resumeText = ResumeJobComparisonService.DEFAULT_RESUME_TEXT,
      targetCompany = razorpayPreset.company,
      targetRole = razorpayPreset.role,
      jobRequirements = razorpayPreset.requirementsText
    )

    val feedback = ResumeJobComparisonService.generateDeterministicComparison(request)

    assertEquals("Razorpay", feedback.targetCompany)
    assertTrue("Overall score for Razorpay should be valid", feedback.overallFitScore in 70..99)
    assertEquals("Razorpay", feedback.companyCultureIntel.companyName)
    assertTrue(feedback.companyCultureIntel.barRaiserFocus.contains("Transaction") || feedback.companyCultureIntel.barRaiserFocus.contains("Root-Cause"))

    // Verify FinTech bullet tailoring
    val hasFintechBullet = feedback.bulletEnhancements.any { it.enhancedBullet.contains("merchant") || it.enhancedBullet.contains("KYC") || it.enhancedBullet.contains("GMV") }
    assertTrue("Should contain FinTech merchant-specific bullet rewrite", hasFintechBullet)
  }

  @Test
  fun testBulletRewriteApplyAndRevert() {
    var resume = ResumeJobComparisonService.DEFAULT_RESUME_TEXT
    val originalBullet = "Spearheaded end-to-end supply chain telemetry redesign across 14 regional fulfillment nodes, slashing order cycle times from 28 minutes to 16.2 minutes (42% reduction)."
    val enhancedBullet = "Engineered sub-10m dark store picking workflow across 14 micro-fulfilment hubs, slashing in-store order cycle time by 42% (28m to 16.2m) and maintaining 99.4% SLA adherence."

    assertTrue("Original resume must contain original bullet", resume.contains(originalBullet))

    // Apply enhancement
    resume = resume.replace(originalBullet, enhancedBullet)
    assertTrue("Resume must now contain enhanced bullet", resume.contains(enhancedBullet))
    assertFalse("Resume must no longer contain old bullet", resume.contains(originalBullet))

    // Revert enhancement
    resume = resume.replace(enhancedBullet, originalBullet)
    assertTrue("Resume must be restored to original bullet", resume.contains(originalBullet))
    assertFalse("Resume must no longer contain enhanced bullet", resume.contains(enhancedBullet))
  }

  @Test
  fun testCustomPastedJobDescriptionMatching() {
    val customJobRequirements = """
      We are hiring a Lead Operations & Strategy Manager to oversee our quick-commerce dark stores in Bengaluru.
      Key Requirements:
      • Minimum 3 years in dark store fulfillment or high-velocity logistics
      • Hands-on proficiency in SQL, Python, and automated dashboards
      • Demonstrated SLA reduction and cycle time optimization
      • Experience managing ₹10L+ budgets and cross-functional field teams
    """.trimIndent()

    val request = ResumeJobComparisonRequest(
      resumeText = ResumeJobComparisonService.DEFAULT_RESUME_TEXT,
      targetCompany = "Blinkit QuickCommerce",
      targetRole = "Lead Operations & Strategy Manager",
      jobRequirements = customJobRequirements
    )

    val feedback = ResumeJobComparisonService.generateDeterministicComparison(request)

    assertEquals("Blinkit QuickCommerce", feedback.targetCompany)
    assertEquals("Lead Operations & Strategy Manager", feedback.targetRole)
    assertTrue("Overall fit score should be high for matching skills", feedback.overallFitScore >= 75)
    assertTrue("ATS compatibility score should be computed", feedback.atsCompatibilityScore > 0)
    assertTrue("Executive summary must be generated", feedback.executiveSummary.isNotBlank())
    assertTrue("Matched requirements must be extracted", feedback.matchedRequirements.isNotEmpty())
    assertTrue("Requirement gaps should be identified", feedback.requirementGaps.isNotEmpty())
    assertTrue("Action plan must provide next steps", feedback.actionPlan.isNotEmpty())
    assertTrue("Side-by-side bullet rewrites must be provided", feedback.bulletEnhancements.isNotEmpty())
  }
}
