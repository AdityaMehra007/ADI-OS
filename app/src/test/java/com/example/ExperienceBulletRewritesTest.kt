package com.example

import com.example.data.model.ExperienceBulletRewrite
import com.example.service.LocalJobActionPlanGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExperienceBulletRewritesTest {

  @Test
  fun testExperienceBulletRewritesGeneratedForQuickCommerce() {
    val zeptoJd = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS.first { it.company == "Zepto" }
    val plan = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = zeptoJd.role,
      targetCompany = zeptoJd.company,
      jobDescription = zeptoJd.description
    )

    assertNotNull(plan)
    val rewrites = plan.experienceBulletRewrites
    assertTrue("Should generate specific bullet rewrites for Quick Commerce", rewrites.isNotEmpty())
    assertEquals(3, rewrites.size)

    rewrites.forEach { rewrite ->
      assertTrue("Target experience area must not be blank", rewrite.targetExperienceArea.isNotBlank())
      assertTrue("Original passive bullet must not be blank", rewrite.originalBullet.isNotBlank())
      assertTrue("Suggested rewrite must not be blank", rewrite.suggestedRewriteBullet.isNotBlank())
      assertTrue("Targeted JD requirement must be populated", rewrite.targetedJdRequirement.isNotBlank())
      assertTrue("Metric infused must be non-empty", rewrite.metricInfused.isNotBlank())
      assertTrue("Keywords injected must not be empty", rewrite.keywordsInjected.isNotEmpty())
      assertTrue("Strategic rationale must explain the transformation", rewrite.strategicRationale.isNotBlank())
      assertFalse("Initial state should not be applied", rewrite.isApplied)
    }

    val firstRewrite = rewrites.first()
    assertTrue("Should focus on pick latency or fulfillment", firstRewrite.targetExperienceArea.contains("Fulfillment", ignoreCase = true) || firstRewrite.targetExperienceArea.contains("Pick", ignoreCase = true))
    assertTrue("Rewrite bullet must contain quantitative improvement metric", firstRewrite.suggestedRewriteBullet.contains("%") || firstRewrite.suggestedRewriteBullet.contains("minutes"))
  }

  @Test
  fun testExperienceBulletRewritesGeneratedForFintech() {
    val razorpayJd = LocalJobActionPlanGenerator.SAMPLE_JOB_DESCRIPTIONS.first { it.company == "Razorpay" }
    val plan = LocalJobActionPlanGenerator.generateActionPlan(
      targetRole = razorpayJd.role,
      targetCompany = razorpayJd.company,
      jobDescription = razorpayJd.description
    )

    assertNotNull(plan)
    val rewrites = plan.experienceBulletRewrites
    assertEquals(3, rewrites.size)

    val paymentConversionRewrite = rewrites.firstOrNull { it.targetExperienceArea.contains("Payment", ignoreCase = true) }
    assertNotNull("Should contain payment conversion rewrite for Razorpay fintech JD", paymentConversionRewrite)
    assertTrue("Payment conversion rewrite should mention success rates or bps", paymentConversionRewrite!!.metricInfused.contains("bps") || paymentConversionRewrite.metricInfused.contains("%"))
    assertTrue("Should inject payment telemetry keywords", paymentConversionRewrite.keywordsInjected.any { it.contains("Payment", ignoreCase = true) || it.contains("Rail", ignoreCase = true) })
  }

  @Test
  fun testExperienceBulletRewritesToggleState() {
    val sampleRewrite = ExperienceBulletRewrite(
      id = "test_rewrite_1",
      targetExperienceArea = "Analytics & Telemetry",
      originalBullet = "Looked at numbers in Excel.",
      suggestedRewriteBullet = "Engineered real-time SQL dashboards tracking 12 core business metrics, accelerating team reporting velocity by 45%.",
      targetedJdRequirement = "SQL Data Analytics & Automated Telemetry",
      metricInfused = "45% reporting velocity lift across 12 metrics",
      keywordsInjected = listOf("SQL", "Dashboards", "Metrics"),
      strategicRationale = "Transforms passive spreadsheet task into high-impact analytics engineering.",
      isApplied = false
    )

    assertFalse(sampleRewrite.isApplied)
    val applied = sampleRewrite.copy(isApplied = true)
    assertTrue(applied.isApplied)
    assertEquals(sampleRewrite.suggestedRewriteBullet, applied.suggestedRewriteBullet)
  }
}
