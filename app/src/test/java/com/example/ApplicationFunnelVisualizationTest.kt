package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Application
import com.example.ui.components.ApplicationFunnelCalculator
import com.example.ui.components.CompanyApplicationSentStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ApplicationFunnelVisualizationTest {

  @Test
  fun testFunnelCalculationWithDefaultTracker() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val nativeApps = listOf(
      Application(
        id = "app_1",
        jobId = "job_1",
        companyName = "Google",
        roleTitle = "Associate Business Analyst",
        status = "INTERVIEW",
        dateDiscovered = "2026-09-01",
        dateApplied = "2026-09-05",
        resumeVersion = "v1",
        coverLetterSnippet = "",
        customAnswersJson = "{}",
        followUpDate = "",
        followUpNotes = "",
        outcomeNotes = ""
      ),
      Application(
        id = "app_2",
        jobId = "job_2",
        companyName = "Swiggy",
        roleTitle = "Strategy & Operations Lead",
        status = "OFFER",
        dateDiscovered = "2026-09-01",
        dateApplied = "2026-09-06",
        resumeVersion = "v1",
        coverLetterSnippet = "",
        customAnswersJson = "{}",
        followUpDate = "",
        followUpNotes = "",
        outcomeNotes = ""
      )
    )

    val report = ApplicationFunnelCalculator.calculateFunnelMetrics(context, nativeApps)

    assertNotNull(report)
    assertTrue("Targeted roles should be positive", report.totalTargetedRoles > 0)
    assertTrue("Emails sent count should be positive", report.emailsSent > 0)
    assertTrue("Email to role conversion rate should be between 0 and 100", report.emailToRoleConversionRate in 0f..100f)
    assertEquals(5, report.stages.size)
    assertEquals("Targeted Roles Identified", report.stages[0].stageName)
    assertEquals("Emails / Outreach Sent ('Application Sent')", report.stages[1].stageName)
    assertTrue("Role breakdown should contain mapped roles", report.roleBreakdown.isNotEmpty())
  }

  @Test
  fun testFunnelCalculatesHigherRateWhenMoreEmailsSent() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = context.getSharedPreferences("titan_app_sent_tracker_prefs", Context.MODE_PRIVATE)

    // Store custom items with 8 sent out of 10
    val customItems = listOf(
      CompanyApplicationSentStatus("Google", isSent = true, role = "Strategy Lead"),
      CompanyApplicationSentStatus("Swiggy", isSent = true, role = "Operations Lead"),
      CompanyApplicationSentStatus("Zepto", isSent = true, role = "Supply Chain Analyst"),
      CompanyApplicationSentStatus("Uber", isSent = true, role = "Operations Manager"),
      CompanyApplicationSentStatus("Bain", isSent = false, role = "Consultant"),
      CompanyApplicationSentStatus("McKinsey", isSent = false, role = "Analyst"),
      CompanyApplicationSentStatus("Stripe", isSent = true, role = "Risk Ops"),
      CompanyApplicationSentStatus("Razorpay", isSent = true, role = "Product Ops"),
      CompanyApplicationSentStatus("CRED", isSent = true, role = "Growth Lead"),
      CompanyApplicationSentStatus("Flipkart", isSent = true, role = "Logistics Lead")
    )
    prefs.edit().putString("stored_company_sent_data", CompanyApplicationSentStatus.listToJsonString(customItems)).commit()

    val report = ApplicationFunnelCalculator.calculateFunnelMetrics(context, emptyList())

    assertEquals(10, report.totalTargetedRoles)
    assertEquals(8, report.emailsSent)
    assertEquals(80.0f, report.emailToRoleConversionRate, 0.01f)
  }
}
