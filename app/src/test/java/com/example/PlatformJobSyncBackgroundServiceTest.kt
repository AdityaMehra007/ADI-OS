package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Application
import com.example.data.model.JobPlatform
import com.example.data.model.PlatformSyncDaemonConfig
import com.example.service.JobPlatformSyncEngine
import com.example.service.PlatformJobSyncBackgroundService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlatformJobSyncBackgroundServiceTest {

  @Test
  fun testJobPlatformEnumAndParsing() {
    assertEquals(JobPlatform.LINKEDIN, JobPlatform.fromKey("LINKEDIN"))
    assertEquals(JobPlatform.GREENHOUSE, JobPlatform.fromKey("greenhouse"))
    assertEquals(JobPlatform.LEVER, JobPlatform.fromKey("lever"))
    assertEquals(JobPlatform.WORKDAY, JobPlatform.fromKey("workday"))
    assertEquals(JobPlatform.INDEED, JobPlatform.fromKey("indeed"))
    assertEquals(JobPlatform.WELLFOUND, JobPlatform.fromKey("wellfound"))
    assertEquals(JobPlatform.DIRECT_CAREERS, JobPlatform.fromKey("direct_careers"))
    assertEquals(JobPlatform.DIRECT_CAREERS, JobPlatform.fromKey("unknown_source"))

    assertTrue(JobPlatform.entries.size >= 7)
    for (platform in JobPlatform.entries) {
      assertNotNull(platform.displayName)
      assertNotNull(platform.logoEmoji)
      assertNotNull(platform.getBrandColor())
    }
  }

  @Test
  fun testPlatformDetectionForApplication() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = JobPlatformSyncEngine.getInstance(context)

    val razorpayApp = Application(
      id = "test_app_rzp",
      jobId = "job_rzp",
      companyName = "Razorpay",
      roleTitle = "Associate Business Operations",
      status = "APPLIED",
      dateDiscovered = "2026-09-18",
      dateApplied = "2026-09-20",
      resumeVersion = "v1.2",
      coverLetterSnippet = "Greenhouse portal",
      customAnswersJson = "{}",
      followUpDate = "2026-09-25",
      followUpNotes = "Greenhouse application portal",
      outcomeNotes = "Active tracking"
    )
    val platform = engine.detectPlatformForApplication(razorpayApp)
    assertEquals(JobPlatform.GREENHOUSE, platform)

    val swiggyApp = Application(
      id = "test_app_swiggy",
      jobId = "job_swiggy",
      companyName = "Swiggy",
      roleTitle = "Strategy Analyst",
      status = "APPLIED",
      dateDiscovered = "2026-09-18",
      dateApplied = "2026-09-20",
      resumeVersion = "v1.0",
      coverLetterSnippet = "Direct",
      customAnswersJson = "{}",
      followUpDate = "2026-09-27",
      followUpNotes = "Applied directly on careers site",
      outcomeNotes = "Active"
    )
    val swiggyPlatform = engine.detectPlatformForApplication(swiggyApp)
    assertEquals(JobPlatform.DIRECT_CAREERS, swiggyPlatform)
  }

  @Test
  fun testSyncEngineExecutionAndTelemetry() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = JobPlatformSyncEngine.getInstance(context)

    val result = engine.executeSyncCycle("TEST_RUNNER")
    assertTrue(result.isSuccess)
    assertTrue(result.savedJobsPolledCount > 0)
    assertTrue(result.platformsPolled.isNotEmpty())
    assertNotNull(result.formattedTime)

    val lastResult = engine.lastSyncResult.value
    assertNotNull(lastResult)
    assertEquals(result.syncId, lastResult?.syncId)

    val healthMap = engine.platformHealthMap.value
    assertTrue(healthMap.containsKey(JobPlatform.LINKEDIN))
    assertTrue(healthMap.containsKey(JobPlatform.GREENHOUSE))
  }

  @Test
  fun testDaemonConfigurationUpdates() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val engine = JobPlatformSyncEngine.getInstance(context)

    val newConfig = PlatformSyncDaemonConfig(
      isPeriodicEnabled = true,
      intervalMinutes = 60
    )
    engine.updateDaemonConfig(newConfig)
    assertEquals(60, engine.daemonConfig.value.intervalMinutes)
    assertTrue(engine.daemonConfig.value.isPeriodicEnabled)
  }

  @Test
  fun testBackgroundServiceHelperMethods() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    // Verify start and stop triggers don't throw
    PlatformJobSyncBackgroundService.startPeriodicSync(context, 30)
    PlatformJobSyncBackgroundService.cancelAlarm(context)
    PlatformJobSyncBackgroundService.stopPeriodicSync(context)
  }
}
