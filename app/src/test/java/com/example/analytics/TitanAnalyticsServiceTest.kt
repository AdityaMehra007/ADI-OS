package com.example.analytics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CorporateApplicationTarget
import com.example.util.KeypadDirectiveRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TitanAnalyticsServiceTest {

  private lateinit var context: Context
  private val testDispatcher = StandardTestDispatcher()
  private val testScope = TestScope(testDispatcher)

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
    context.getSharedPreferences("titan_analytics_popularity_prefs", Context.MODE_PRIVATE)
      .edit()
      .clear()
      .commit()
  }

  @Test
  fun `initial popularity report provides default ranked career strategies`() {
    val service = TitanAnalyticsService(context, testScope)
    val report = service.popularityReport.value

    assertNotNull(report.topStrategy)
    assertEquals(5, report.strategies.size)
    assertEquals(1, report.topStrategy?.popularityRank)
    assertEquals(TitanAnalyticsService.STRATEGY_OPS_FULFILLMENT, report.topStrategy?.strategyId)
    assertTrue(report.strategies.all { it.popularityScore > 0 })
  }

  @Test
  fun `triggering 25-keypad directive increments telemetry and updates strategy popularity`() = runTest {
    val service = TitanAnalyticsService(context, testScope)
    val directive = KeypadDirectiveRegistry.findByCode("07") // Real-Time Interview HUD
    assertNotNull(directive)

    val initialScore = service.popularityReport.value.strategies
      .first { it.strategyId == TitanAnalyticsService.STRATEGY_OPS_FULFILLMENT }
      .popularityScore

    service.trackKeypadDirectiveTriggered(directive!!)

    val updatedReport = service.popularityReport.value
    val updatedScore = updatedReport.strategies
      .first { it.strategyId == TitanAnalyticsService.STRATEGY_OPS_FULFILLMENT }
      .popularityScore

    assertTrue("Score should increase after directive execution", updatedScore > initialScore)
    assertTrue("Total keypad executions should be at least 1", updatedReport.totalKeypadExecutions >= 1)
  }

  @Test
  fun `1-click corporate apply dispatch boosts targeted career strategy score`() = runTest {
    val service = TitanAnalyticsService(context, testScope)
    val aiStrategyBefore = service.popularityReport.value.strategies
      .first { it.strategyId == TitanAnalyticsService.STRATEGY_AI_OPS_AUTOMATION }
      .popularityScore

    service.trackOneClickApplyDispatched(
      companyName = "Zepto AI Labs",
      jobTitle = "AI Operations & Prompt Automation Lead",
      matchScore = 98,
      ctcLakhs = 35.0,
      careerStrategy = TitanAnalyticsService.STRATEGY_AI_OPS_AUTOMATION
    )

    val aiStrategyAfter = service.popularityReport.value.strategies
      .first { it.strategyId == TitanAnalyticsService.STRATEGY_AI_OPS_AUTOMATION }
      .popularityScore

    assertTrue(
      "AI Strategy score should increase by at least 25 points after 1-click dispatch",
      aiStrategyAfter >= aiStrategyBefore + 25
    )
  }

  @Test
  fun `corporate target saved correctly attributes to appropriate career strategy`() = runTest {
    val service = TitanAnalyticsService(context, testScope)
    val target = CorporateApplicationTarget(
      id = "test_target_blinkit",
      companyName = "Blinkit",
      targetRoleTitle = "Dark Store Expansion Specialist",
      priorityTier = "TIER_1_APEX",
      targetCtcLakhs = 28.0,
      applicationStatus = "TARGETED",
      alignmentScore = 96
    )

    service.trackCorporateTargetSaved(target)

    val report = service.popularityReport.value
    val opsStrategy = report.strategies.first { it.strategyId == TitanAnalyticsService.STRATEGY_OPS_FULFILLMENT }
    assertTrue(opsStrategy.oneClickApplicationsDispatchedCount >= 1)
  }

  @Test
  fun `mapping helpers accurately route directives and companies to strategic pillars`() {
    val service = TitanAnalyticsService(context, testScope)

    val hudDirective = KeypadDirectiveRegistry.findByCode("07")!!
    assertEquals(
      TitanAnalyticsService.STRATEGY_OPS_FULFILLMENT,
      service.mapDirectiveToStrategy(hudDirective)
    )

    val jdDirective = KeypadDirectiveRegistry.findByCode("01")!!
    assertEquals(
      TitanAnalyticsService.STRATEGY_AI_OPS_AUTOMATION,
      service.mapDirectiveToStrategy(jdDirective)
    )

    val cxoDirective = KeypadDirectiveRegistry.findByCode("04")!!
    assertEquals(
      TitanAnalyticsService.STRATEGY_BANGALORE_CXO,
      service.mapDirectiveToStrategy(cxoDirective)
    )

    val salaryDirective = KeypadDirectiveRegistry.findByCode("13")!!
    assertEquals(
      TitanAnalyticsService.STRATEGY_OFFER_TACTICIAN,
      service.mapDirectiveToStrategy(salaryDirective)
    )

    assertEquals(
      TitanAnalyticsService.STRATEGY_OPS_FULFILLMENT,
      service.mapCompanyAndRoleToStrategy("Zepto", "Dark Store Operations Lead")
    )

    assertEquals(
      TitanAnalyticsService.STRATEGY_AI_OPS_AUTOMATION,
      service.mapCompanyAndRoleToStrategy("Tech Corp", "AI Automation Lead")
    )
  }
}
