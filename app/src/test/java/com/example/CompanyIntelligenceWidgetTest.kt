package com.example

import com.example.ai.CompanyResearchFocus
import com.example.ai.CompanyResearchResponse
import com.example.data.TitanDatabase
import com.example.data.model.Company
import com.example.data.model.CompanyIntelReport
import com.example.data.model.CompanyIntelligenceWidgetCache
import com.example.data.model.GroundedSource
import com.example.data.model.toCompanyIntelReport
import com.example.data.model.toWidgetCache
import androidx.room.Room
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CompanyIntelligenceWidgetTest {

  @Test
  fun testCompanyIntelReport_groundedStructure() {
    val sources = listOf(
      GroundedSource(
        title = "Zepto Raises $665M Pre-IPO Round",
        url = "https://techcrunch.com/zepto-funding",
        snippet = "Quick commerce leader Zepto expands dark store footprint."
      ),
      GroundedSource(
        title = "Zepto Corporate Hub Bellandur",
        url = "https://zeptonow.com/press",
        snippet = "Bengaluru engineering headquarters hiring."
      )
    )

    val report = CompanyIntelReport(
      companyName = "Zepto",
      timestamp = "18 Sep 2026, 11:30 AM",
      businessNewsSummary = "Zepto prepares for 2026 public listing with accelerating GMV.",
      hiringUpdates = listOf(
        "ACCELERATING hiring in Bengaluru across Supply Chain Analytics & Dark Store Ops",
        "12 open strategic roles tracked"
      ),
      executiveShifts = listOf(
        "Appointed new VP of Supply Chain Automation from Amazon"
      ),
      strategicRisksAndOpportunities = "10-minute delivery unit economics turning EBITDA positive.",
      interviewAngles = listOf(
        "Highlight SQL automated demand forecasting and 42% cycle reduction in logistics."
      ),
      searchQueriesUsed = listOf(
        "Zepto latest business news 2026",
        "Zepto funding GMV IPO Bellandur hiring"
      ),
      groundedSources = sources,
      isLiveGrounded = true,
      confidenceScore = 96
    )

    assertEquals("Zepto", report.companyName)
    assertTrue(report.isLiveGrounded)
    assertEquals(96, report.confidenceScore)
    assertEquals(2, report.groundedSources.size)
    assertEquals(2, report.searchQueriesUsed.size)
    assertEquals("Zepto Raises $665M Pre-IPO Round", report.groundedSources.first().title)
    assertTrue(report.businessNewsSummary.contains("public listing"))
  }

  @Test
  fun testCompanyResearchResponse_toCompanyIntelReport_conversion() {
    val response = CompanyResearchResponse(
      companyName = "Razorpay",
      focusArea = CompanyResearchFocus.ALL,
      executiveSummary = "Fintech powerhouse powering payments for millions of businesses.",
      businessNewsSummary = "Razorpay crosses \$150B in annualized TPV and expands into SE Asia.",
      hiringTrends = listOf("Expanding payment gateway operations team in Koramangala, Bengaluru"),
      leadershipUpdates = listOf("Founder directive focusing on offline POS and UPI switch reliability"),
      strategicRisksAndOpportunities = "RBI payment aggregator license secured, tailwinds in cross-border settlements.",
      interviewPreparationAngles = listOf("Present metrics on dispute reconciliation workflows and API latency."),
      competitiveMoats = listOf("Highest conversion rates and direct bank switch integrations"),
      recommendedOutreachPitch = "Saw the recent SE Asia expansion; would love to share how my analytics pipelines reduced operational cycle time by 42%.",
      groundedSources = listOf(
        GroundedSource("Razorpay Press", "https://razorpay.com/news")
      ),
      searchQueriesUsed = listOf("Razorpay latest financial metrics 2026"),
      confidenceScore = 98,
      timestamp = "18 Sep 2026, 11:40 AM",
      isLiveGrounded = true,
      rawGeminiOutput = "{}"
    )

    val converted = response.toCompanyIntelReport()
    assertEquals("Razorpay", converted.companyName)
    assertEquals("Razorpay crosses \$150B in annualized TPV and expands into SE Asia.", converted.businessNewsSummary)
    assertEquals(1, converted.hiringUpdates.size)
    assertEquals(1, converted.groundedSources.size)
    assertTrue(converted.isLiveGrounded)
    assertEquals(98, converted.confidenceScore)
  }

  @Test
  fun testSavedCompanyNewsSummary_cachedPersistence() {
    val company = Company(
      id = "comp_swiggy_test",
      name = "Swiggy",
      website = "https://swiggy.com",
      careersUrl = "https://careers.swiggy.com",
      industry = "Food Delivery & Quick Commerce",
      tier = "S",
      strategicPriority = "DREAM",
      hiringVelocity = "ACCELERATING",
      isWatched = true,
      latestNewsSummary = "Swiggy Instamart dark stores achieve unit profitability in top 6 metros.",
      newsSummaryTimestamp = "18 Sep 2026, 10:00 AM"
    )

    assertTrue(company.isWatched)
    assertEquals("ACCELERATING", company.hiringVelocity)
    assertEquals("Swiggy Instamart dark stores achieve unit profitability in top 6 metros.", company.latestNewsSummary)
    assertFalse(company.newsSummaryTimestamp.isBlank())
  }

  @Test
  fun testLaunchCompanyNewsShareSheet_createsValidShareSheetIntent() {
    val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
    val companyName = "CRED"
    val newsSummary = "CRED launches unified garage vehicle management platform with 40% surge in MAUs."

    com.example.ui.components.launchCompanyNewsShareSheet(
      context = context,
      companyName = companyName,
      newsSummary = newsSummary,
      hiringUpdates = listOf("Hiring Data Scientists in Indiranagar"),
      interviewAngle = "Focus on zero-latency credit card transaction reconciliation",
      sources = listOf(GroundedSource("CRED Press", "https://cred.club/news")),
      timestamp = "18 Sep 2026, 12:00 PM"
    )

    val shadowApp = org.robolectric.Shadows.shadowOf(context as android.app.Application)
    val startedIntent = shadowApp.nextStartedActivity
    assertNotNull(startedIntent)
    assertEquals(android.content.Intent.ACTION_CHOOSER, startedIntent.action)

    val targetIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      startedIntent.getParcelableExtra(android.content.Intent.EXTRA_INTENT, android.content.Intent::class.java)
    } else {
      @Suppress("DEPRECATION")
      startedIntent.getParcelableExtra(android.content.Intent.EXTRA_INTENT) as? android.content.Intent
    }

    assertNotNull(targetIntent)
    assertEquals(android.content.Intent.ACTION_SEND, targetIntent?.action)
    assertEquals("text/plain", targetIntent?.type)
    val sharedText = targetIntent?.getStringExtra(android.content.Intent.EXTRA_TEXT) ?: ""
    assertTrue(sharedText.contains("CRED • Corporate Intelligence & AI News Summary"))
    assertTrue(sharedText.contains(newsSummary))
    assertTrue(sharedText.contains("Hiring Data Scientists in Indiranagar"))
    assertTrue(sharedText.contains("https://cred.club/news"))
  }

  @Test
  fun testCompanyIntelligenceWidgetCache_roomDao_persistAndRetrieve() = runBlocking {
    val application = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
    val database = Room.inMemoryDatabaseBuilder(
      application,
      TitanDatabase::class.java
    ).allowMainThreadQueries().build()

    try {
      val dao = database.companyIntelligenceWidgetDao()

      val cacheZepto = CompanyIntelligenceWidgetCache(
        companyName = "Zepto",
        cachedSummary = "Zepto prepares for public listing with accelerating GMV.",
        timestamp = "18 Sep 2026, 11:30 AM",
        hiringUpdates = "Expanding Bellandur dark stores",
        interviewAngle = "Focus on dark store picking metrics",
        executiveShifts = "New supply chain VP",
        strategicRisks = "Margin pressure from quick commerce",
        sourcesJson = "Zepto Press|||https://zeptonow.com",
        confidenceScore = 96,
        isLiveGrounded = true,
        isLastSelected = true,
        lastUpdatedMillis = System.currentTimeMillis()
      )

      dao.insertOrUpdate(cacheZepto)

      // Verify retrieval by company name
      val fetched = dao.getCacheForCompany("Zepto")
      assertNotNull(fetched)
      assertEquals("Zepto", fetched?.companyName)
      assertEquals("Zepto prepares for public listing with accelerating GMV.", fetched?.cachedSummary)
      assertTrue(fetched?.isLastSelected == true)

      // Verify Flow emission
      val lastSelected = dao.getLastSelectedWidgetCacheFlow().first()
      assertNotNull(lastSelected)
      assertEquals("Zepto", lastSelected?.companyName)

      // Convert back to CompanyIntelReport
      val report = fetched!!.toCompanyIntelReport()
      assertEquals("Zepto", report.companyName)
      assertEquals("Zepto prepares for public listing with accelerating GMV.", report.businessNewsSummary)
      assertTrue(report.isLiveGrounded)
      assertEquals(1, report.groundedSources.size)
      assertEquals("Zepto Press", report.groundedSources.first().title)
      assertEquals("https://zeptonow.com", report.groundedSources.first().url)
    } finally {
      database.close()
    }
  }

  @Test
  fun testCompanyIntelligenceWidgetCache_roomDao_markLastSelected() = runBlocking {
    val application = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
    val database = Room.inMemoryDatabaseBuilder(
      application,
      TitanDatabase::class.java
    ).allowMainThreadQueries().build()

    try {
      val dao = database.companyIntelligenceWidgetDao()

      dao.insertOrUpdate(
        CompanyIntelligenceWidgetCache(
          companyName = "Zepto",
          cachedSummary = "Zepto quick commerce news.",
          isLastSelected = true
        )
      )
      dao.insertOrUpdate(
        CompanyIntelligenceWidgetCache(
          companyName = "Swiggy",
          cachedSummary = "Swiggy Instamart expansion.",
          isLastSelected = false
        )
      )

      assertEquals("Zepto", dao.getLastSelectedWidgetCache()?.companyName)

      // User selects Swiggy in widget
      dao.markCompanyAsLastSelected("Swiggy")

      val updatedSelected = dao.getLastSelectedWidgetCache()
      assertNotNull(updatedSelected)
      assertEquals("Swiggy", updatedSelected?.companyName)
      assertEquals("Swiggy Instamart expansion.", updatedSelected?.cachedSummary)

      val zeptoCache = dao.getCacheForCompany("Zepto")
      assertFalse(zeptoCache?.isLastSelected ?: true)
    } finally {
      database.close()
    }
  }

  @Test
  fun testCompanyIntelligenceWidgetCache_modelConverters() {
    val report = CompanyIntelReport(
      companyName = "Razorpay",
      timestamp = "18 Sep 2026, 12:15 PM",
      businessNewsSummary = "Razorpay powers \$150B in TPV with Southeast Asia expansion.",
      hiringUpdates = listOf("Hiring Senior Product Managers in Koramangala"),
      executiveShifts = listOf("UPI payment stack overhaul led by engineering director"),
      strategicRisksAndOpportunities = "RBI licenses open up payment aggregator scaling.",
      interviewAngles = listOf("Demonstrate transaction latency reduction and idempotency keys."),
      searchQueriesUsed = listOf("Razorpay business metrics 2026"),
      groundedSources = listOf(
        GroundedSource("Razorpay Insights", "https://razorpay.com/insights")
      ),
      isLiveGrounded = true,
      confidenceScore = 98
    )

    val cache = report.toWidgetCache(isLastSelected = true)
    assertEquals("Razorpay", cache.companyName)
    assertEquals("Razorpay powers \$150B in TPV with Southeast Asia expansion.", cache.cachedSummary)
    assertTrue(cache.isLastSelected)
    assertTrue(cache.sourcesJson.contains("Razorpay Insights|||https://razorpay.com/insights"))

    val restoredReport = cache.toCompanyIntelReport()
    assertEquals("Razorpay", restoredReport.companyName)
    assertEquals("Razorpay powers \$150B in TPV with Southeast Asia expansion.", restoredReport.businessNewsSummary)
    assertEquals(1, restoredReport.groundedSources.size)
    assertEquals("Razorpay Insights", restoredReport.groundedSources[0].title)
    assertEquals("https://razorpay.com/insights", restoredReport.groundedSources[0].url)
    assertEquals(98, restoredReport.confidenceScore)
    assertTrue(restoredReport.isLiveGrounded)
  }

  @Test
  fun testCompanyIntelligenceWidgetCache_fetchingFallbackAndPlaceholder() {
    val emptyCache = CompanyIntelligenceWidgetCache(
      companyName = "Swiggy",
      cachedSummary = "",
      timestamp = ""
    )
    val restoredEmpty = emptyCache.toCompanyIntelReport()
    assertEquals("Swiggy", restoredEmpty.companyName)
    assertEquals(0, restoredEmpty.confidenceScore)
    assertFalse(restoredEmpty.isLiveGrounded)
  }
}
