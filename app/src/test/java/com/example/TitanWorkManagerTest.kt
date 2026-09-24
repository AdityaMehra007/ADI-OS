package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.service.work.JobDiscoveryWorker
import com.example.service.work.LinkedInSyncWorker
import com.example.service.work.PlatformJobSyncWorker
import com.example.service.work.TitanWorkManagerHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TitanWorkManagerTest {

  @Test
  fun testWorkerConstantsAndIdentifiers() {
    assertEquals("titan_job_discovery_periodic_work", JobDiscoveryWorker.UNIQUE_WORK_NAME)
    assertEquals("JobDiscoveryWorker", JobDiscoveryWorker.TAG)

    assertEquals("titan_linkedin_periodic_sync_work", LinkedInSyncWorker.UNIQUE_WORK_NAME)
    assertEquals("LinkedInSyncWorker", LinkedInSyncWorker.TAG)

    assertEquals("titan_platform_job_periodic_sync_work", PlatformJobSyncWorker.UNIQUE_WORK_NAME)
    assertEquals("PlatformJobSyncWorker", PlatformJobSyncWorker.TAG)
  }

  @Test
  fun testWorkManagerHelperInitializationDoesNotThrow() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertNotNull(context)

    // Verify helper executes safely on context
    TitanWorkManagerHelper.scheduleAllPeriodicTasks(
      context = context,
      jobDiscoveryIntervalMinutes = 30L,
      linkedInSyncIntervalHours = 4L,
      platformSyncIntervalMinutes = 30L
    )

    // Verify cancellation executes safely
    TitanWorkManagerHelper.cancelAllPeriodicTasks(context)
  }
}
