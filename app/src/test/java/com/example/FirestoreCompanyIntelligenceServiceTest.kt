package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CompanyIntelReport
import com.example.data.model.GroundedSource
import com.example.service.FirestoreCompanyIntelligenceService
import com.example.service.FirestoreSyncState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FirestoreCompanyIntelligenceServiceTest {

  private lateinit var context: Context
  private lateinit var service: FirestoreCompanyIntelligenceService

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    service = FirestoreCompanyIntelligenceService(context)
  }

  @Test
  fun testFetchCompanyIntelFromFirestore_returnsValidReportAndSyncs() = runBlocking {
    val result = service.fetchCompanyIntelFromFirestore("user_test_123", "Zepto")
    assertTrue(result.isSuccess)

    val report = result.getOrNull()
    assertNotNull(report)
    assertEquals("Zepto", report?.companyName)
    assertTrue(report?.hiringUpdates?.isNotEmpty() == true)
    assertTrue(report?.interviewAngles?.isNotEmpty() == true)
    assertEquals(FirestoreSyncState.SYNCED, service.syncState.value)
    assertEquals(1.0f, service.syncProgress.value, 0.01f)
    assertNotNull(service.lastSyncedTimestamp.value)
  }

  @Test
  fun testSaveAndFetchCustomCompanyIntel() = runBlocking {
    val customReport = CompanyIntelReport(
      companyName = "Stripe",
      timestamp = "2026-09-13 14:00",
      businessNewsSummary = "Stripe processed over \$1T in global payment volume.",
      hiringUpdates = listOf("Hiring Senior Staff Infrastructure Engineers"),
      executiveShifts = listOf("New Chief Product Officer appointed"),
      strategicRisksAndOpportunities = "Massive tailwinds in enterprise billing APIs",
      interviewAngles = listOf("Discuss idempotent API design and low latency ledger architectures"),
      searchQueriesUsed = listOf("Stripe infrastructure engineering 2026"),
      groundedSources = listOf(
        GroundedSource("Stripe Press", "https://stripe.com/news", "Record volumes and expansion")
      ),
      isLiveGrounded = true,
      confidenceScore = 97
    )

    val saveResult = service.saveCompanyIntelToFirestore("user_test_123", customReport)
    assertTrue(saveResult.isSuccess)

    val fetchResult = service.fetchCompanyIntelFromFirestore("user_test_123", "Stripe")
    assertTrue(fetchResult.isSuccess)

    val fetched = fetchResult.getOrNull()
    assertNotNull(fetched)
    assertEquals("Stripe", fetched?.companyName)
    assertEquals(customReport.businessNewsSummary, fetched?.businessNewsSummary)
  }

  @Test
  fun testFetchAllCompanyIntelFromFirestore() = runBlocking {
    val result = service.fetchAllCompanyIntelFromFirestore("user_test_123")
    assertTrue(result.isSuccess)
    val list = result.getOrNull()
    assertNotNull(list)
    assertTrue(list!!.isNotEmpty())
    assertEquals(FirestoreSyncState.SYNCED, service.syncState.value)
  }
}
