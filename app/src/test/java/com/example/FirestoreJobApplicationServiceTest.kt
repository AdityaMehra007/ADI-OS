package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Application
import com.example.service.FirestoreJobApplicationService
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
class FirestoreJobApplicationServiceTest {

  private lateinit var context: Context
  private lateinit var service: FirestoreJobApplicationService

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    service = FirestoreJobApplicationService(context)
  }

  private fun createSampleApp(id: String, company: String, role: String, status: String): Application {
    return Application(
      id = id,
      jobId = "job_$id",
      companyName = company,
      roleTitle = role,
      status = status,
      dateDiscovered = "2026-09-11",
      dateApplied = "2026-09-11",
      resumeVersion = "v3.2-Strategy-Focused",
      coverLetterSnippet = "Direct submission logged via Titan CRM.",
      customAnswersJson = "{}",
      followUpDate = "2026-09-18",
      followUpNotes = "Connect with hiring manager on LinkedIn.",
      outcomeNotes = "Active pipeline entry tracked."
    )
  }

  @Test
  fun testSaveAndLoadApplicationFromLocalCache() = runBlocking {
    val app1 = createSampleApp("app_1", "Google", "Associate Product Manager", "APPLIED")
    val app2 = createSampleApp("app_2", "Stripe", "Strategy & Operations Lead", "INTERVIEW")

    // Save directly to local cache
    service.saveAllToLocalCache(listOf(app1, app2), System.currentTimeMillis())

    val loaded = service.loadFromLocalCache()
    assertEquals(2, loaded.size)
    assertEquals("Google", loaded[0].companyName)
    assertEquals("APPLIED", loaded[0].status)
    assertEquals("Stripe", loaded[1].companyName)
    assertEquals("INTERVIEW", loaded[1].status)
  }

  @Test
  fun testSaveApplicationToFirestoreService() = runBlocking {
    val app = createSampleApp("app_test_1", "McKinsey", "Associate Consultant", "ASSESSMENT")
    val result = service.saveApplicationToFirestore("test_user_456", app)

    assertTrue(result.isSuccess)

    // Verify cache reflection
    val cached = service.loadFromLocalCache()
    assertTrue(cached.any { it.companyName == "McKinsey" && it.status == "ASSESSMENT" })
  }

  @Test
  fun testUpdateApplicationStatusInFirestoreService() = runBlocking {
    val app = createSampleApp("app_test_2", "Swiggy", "Senior Business Analyst", "APPLIED")
    service.saveApplicationToFirestore("test_user_456", app)

    // Update status to OFFER
    val updateResult = service.updateApplicationStatusInFirestore("test_user_456", "app_test_2", "OFFER")
    assertTrue(updateResult.isSuccess)

    val cached = service.loadFromLocalCache()
    val updatedApp = cached.find { it.id == "app_test_2" }
    assertNotNull(updatedApp)
    assertEquals("OFFER", updatedApp?.status)
  }

  @Test
  fun testDeleteApplicationFromFirestoreService() = runBlocking {
    val app = createSampleApp("app_test_delete", "Uber", "Operations Manager", "REJECTED")
    service.saveApplicationToFirestore("test_user_456", app)

    val cachedBefore = service.loadFromLocalCache()
    assertTrue(cachedBefore.any { it.id == "app_test_delete" })

    // Delete
    val deleteResult = service.deleteApplicationFromFirestore("test_user_456", "app_test_delete")
    assertTrue(deleteResult.isSuccess)

    val cachedAfter = service.loadFromLocalCache()
    assertTrue(cachedAfter.none { it.id == "app_test_delete" })
  }

  @Test
  fun testBatchSyncApplications() = runBlocking {
    val apps = listOf(
      createSampleApp("batch_1", "Bain & Company", "Associate Consultant", "INTERVIEW"),
      createSampleApp("batch_2", "Microsoft", "Chief of Staff Analyst", "OFFER")
    )

    val syncResult = service.syncAllApplicationsToFirestore("test_user_456", apps)
    assertTrue(syncResult.isSuccess)

    val loaded = service.loadFromLocalCache()
    assertEquals(2, loaded.size)
    assertEquals("Bain & Company", loaded[0].companyName)
    assertEquals("INTERVIEW", loaded[0].status)
    assertEquals("Microsoft", loaded[1].companyName)
    assertEquals("OFFER", loaded[1].status)
  }
}
