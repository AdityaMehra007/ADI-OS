package com.example

import com.example.ui.components.CompanyApplicationSentStatus
import com.example.ui.components.buildCompanySentTrackerHtml
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
class CompanyApplicationSentTrackerTest {

  @Test
  fun testCompanyApplicationSentStatusSerialization() {
    val status = CompanyApplicationSentStatus(
      companyName = "Google",
      isSent = true,
      role = "Associate Business Analyst",
      sentDate = "2026-09-28",
      channel = "Careers Portal",
      notes = "Direct application sent"
    )

    val json = status.toJson()
    val restored = CompanyApplicationSentStatus.fromJson(json)

    assertEquals("Google", restored.companyName)
    assertTrue(restored.isSent)
    assertEquals("Associate Business Analyst", restored.role)
    assertEquals("2026-09-28", restored.sentDate)
    assertEquals("Careers Portal", restored.channel)
    assertEquals("Direct application sent", restored.notes)
  }

  @Test
  fun testListSerializationAndDeserialization() {
    val items = listOf(
      CompanyApplicationSentStatus(companyName = "Swiggy", isSent = true, role = "Strategy Lead"),
      CompanyApplicationSentStatus(companyName = "Bain", isSent = false, role = "Associate Consultant"),
      CompanyApplicationSentStatus(companyName = "Zepto", isSent = true, role = "Ops Lead")
    )

    val jsonString = CompanyApplicationSentStatus.listToJsonString(items)
    val restoredList = CompanyApplicationSentStatus.listFromJsonString(jsonString)

    assertEquals(3, restoredList.size)
    assertEquals("Swiggy", restoredList[0].companyName)
    assertTrue(restoredList[0].isSent)
    assertEquals("Bain", restoredList[1].companyName)
    assertFalse(restoredList[1].isSent)
    assertEquals("Zepto", restoredList[2].companyName)
    assertTrue(restoredList[2].isSent)
  }

  @Test
  fun testDefaultInitialCompaniesNotEmpty() {
    val defaults = CompanyApplicationSentStatus.getDefaultInitialCompanies()
    assertTrue("Default companies list should have items", defaults.isNotEmpty())

    val sentItems = defaults.filter { it.isSent }
    val pendingItems = defaults.filter { !it.isSent }

    assertTrue("Should include both sent and pending items", sentItems.isNotEmpty())
    assertTrue("Should include pending companies", pendingItems.isNotEmpty())
  }

  @Test
  fun testHtmlGeneratorIncludesLocalStorageApiAndStorageKey() {
    val items = CompanyApplicationSentStatus.getDefaultInitialCompanies()
    val jsonString = CompanyApplicationSentStatus.listToJsonString(items)
    val html = buildCompanySentTrackerHtml(jsonString)

    assertNotNull(html)
    assertTrue("HTML must contain window.localStorage.getItem", html.contains("window.localStorage.getItem"))
    assertTrue("HTML must contain window.localStorage.setItem", html.contains("window.localStorage.setItem"))
    assertTrue("HTML must reference storage key", html.contains("titan_company_application_sent_tracker_v1"))
    assertTrue("HTML must include 'Application Sent' label", html.contains("Application Sent"))
    assertTrue("HTML must contain toggleSent handler", html.contains("toggleSent"))
    assertTrue("HTML must contain bridge dispatch", html.contains("AndroidBridge"))
    assertTrue("HTML must include company-item transition", html.contains(".company-item:hover"))
    assertTrue("HTML must include translateY on hover", html.contains("translateY(-2px)"))
  }
}
