package com.example

import com.example.ai.CareerStrategyAdviceRequest
import com.example.ai.CompanyResearchFocus
import com.example.ai.CompanyResearchRequest
import com.example.ai.GeminiServiceWrapper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GeminiParserRobustnessTest {

  @Test
  fun testMarkdownHeaderResilienceForCompanyResearch() = runBlocking {
    val service = GeminiServiceWrapper()
    val request = CompanyResearchRequest(
      companyName = "Zepto",
      focus = CompanyResearchFocus.ALL
    )

    // Verify service handles company research smoothly
    val result = service.researchCompany(request)
    assertTrue("Company research should succeed", result.isSuccess)

    val report = result.getOrNull()
    assertNotNull(report)
    assertEquals("Zepto", report?.companyName)
    assertTrue(report?.hiringTrends?.isNotEmpty() == true)
    assertTrue(report?.interviewAngles?.isNotEmpty() == true)
    assertFalse(report?.executiveSummary.isNullOrBlank())
  }

  @Test
  fun testMarkdownHeaderResilienceForCareerStrategy() = runBlocking {
    val service = GeminiServiceWrapper()
    val request = CareerStrategyAdviceRequest(
      questionOrGoal = "Command ₹18L+ Strategy & Ops role in Bengaluru quick-commerce"
    )

    val result = service.adviseCareerStrategy(request)
    assertTrue("Career strategy advice should succeed", result.isSuccess)

    val advice = result.getOrNull()
    assertNotNull(advice)
    assertFalse(advice?.executiveVerdict.isNullOrBlank())
    assertTrue(advice?.immediate30DayActions?.isNotEmpty() == true)
    assertTrue(advice?.negotiationTactics?.isNotEmpty() == true)
  }
}
