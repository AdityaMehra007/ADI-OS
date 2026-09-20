package com.example

import com.example.data.model.UserProfile
import com.example.data.model.VerifiedFact
import com.example.ui.components.CareerSeniority
import com.example.ui.components.MarketTier
import com.example.util.SalaryBenchmarkNegotiationUtility
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SalaryBenchmarkNegotiationTest {

  @Test
  fun testDetectMarketTier() {
    val tierGoogle = SalaryBenchmarkNegotiationUtility.detectMarketTier("Google", "Distributed Systems Engineer")
    assertEquals(MarketTier.BIG_TECH_FAANG, tierGoogle)

    val tierZepto = SalaryBenchmarkNegotiationUtility.detectMarketTier("Zepto", "Lead Dark Store Operations")
    assertEquals(MarketTier.UNICORN_HIGH_GROWTH, tierZepto)

    val tierSalesforce = SalaryBenchmarkNegotiationUtility.detectMarketTier("Salesforce", "Enterprise Value Strategist")
    assertEquals(MarketTier.ENTERPRISE_SAAS, tierSalesforce)

    val tierStartup = SalaryBenchmarkNegotiationUtility.detectMarketTier("Stealth AI", "Founding Engineer Series A")
    assertEquals(MarketTier.EARLY_STAGE, tierStartup)
  }

  @Test
  fun testDetectSeniority() {
    val staffLevel = SalaryBenchmarkNegotiationUtility.detectSeniority("Staff Backend Engineer", "Direct technical strategy")
    assertEquals(CareerSeniority.STAFF_PRINCIPAL, staffLevel)

    val seniorLevel = SalaryBenchmarkNegotiationUtility.detectSeniority("Senior Product Analyst", "Lead cross-functional roadmap")
    assertEquals(CareerSeniority.SENIOR, seniorLevel)

    val midLevel = SalaryBenchmarkNegotiationUtility.detectSeniority("Business Operations Specialist II", "Manage daily metrics", 3)
    assertEquals(CareerSeniority.MID_LEVEL, midLevel)

    val associateLevel = SalaryBenchmarkNegotiationUtility.detectSeniority("Associate Business Analyst", "Entry level metrics tracking", 1)
    assertEquals(CareerSeniority.EARLY_CAREER, associateLevel)
  }

  @Test
  fun testAnalyzeSalaryBenchmarkAndBundleGeneration() {
    val profile = UserProfile(
      name = "Adi",
      careerScore = 90,
      careerCapitalScore = 88,
      aiLeverageScore = 94,
      totalActiveOffers = 1
    )

    val verifiedFacts = listOf(
      VerifiedFact(category = "EXPERIENCE", claim = "Optimized dark store throughput by 24%", evidenceDetails = "Zepto ops review", tags = "OPS")
    )

    val bundle = SalaryBenchmarkNegotiationUtility.generateDeterministicNegotiationBundle(
      targetRole = "Lead - Strategy & Dark Store Operations",
      targetCompany = "Zepto",
      jobDescription = "Quick commerce fulfillment throughput and unit economics.",
      userProfile = profile,
      verifiedFacts = verifiedFacts,
      currentCtcLakhs = 20.0
    )

    assertNotNull(bundle)
    assertEquals("Zepto", bundle.targetCompany)
    assertEquals(3, bundle.scenarios.size)
    assertNotNull(bundle.phoneCallGuide)

    // Verify benchmark calculations
    val analysis = bundle.benchmarkAnalysis
    assertEquals(MarketTier.UNICORN_HIGH_GROWTH, analysis.detectedTier)
    assertTrue("Leverage score should be high", analysis.candidateLeverageScore >= 75)
    assertTrue("75th percentile should exceed 50th percentile", analysis.p75TotalLakhs > analysis.p50TotalLakhs)
    assertTrue("90th percentile should exceed 75th percentile", analysis.p90TotalLakhs > analysis.p75TotalLakhs)

    // Verify scenario content
    val counterScenario = bundle.scenarios.first { it.scenarioKey == "INITIAL_COUNTER" }
    assertTrue(counterScenario.scriptBody.contains("Zepto"))
    assertTrue(counterScenario.scriptBody.contains("Base Salary"))
    assertTrue(counterScenario.subjectLine.contains("Adi"))

    val phoneGuide = bundle.phoneCallGuide
    assertTrue(phoneGuide.openingHook.isNotBlank())
    assertTrue(phoneGuide.theTargetAnchorAsk.contains("Zepto"))
    assertTrue(phoneGuide.objectionHandlingBudgetCeiling.isNotBlank())
    assertTrue(phoneGuide.closingCommitment.contains("24 hours"))
  }
}
